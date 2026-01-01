package com.example.srlappexperiment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.srlappexperiment.data.analytics.AnalyticsManager
import com.example.srlappexperiment.data.local.database.entities.StudySession
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.data.remote.ai.GeminiManager
import com.example.srlappexperiment.data.remote.ai.VocabularyExplanation
import com.example.srlappexperiment.domain.model.SessionProgress
import com.example.srlappexperiment.domain.model.SessionStats
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import com.example.srlappexperiment.domain.repository.AuthRepository
import com.example.srlappexperiment.domain.usecase.SpacedRepetitionEngine
import com.example.srlappexperiment.util.Constants
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Vocabulary Practice Screen
 * Manages session state, card reviews, and statistics
 */
@HiltViewModel
class VocabularyPracticeViewModel @Inject constructor(
    private val spacedRepetitionEngine: SpacedRepetitionEngine,
    private val vocabularyRepository: VocabularyRepository,
    private val authRepository: AuthRepository,
    private val analyticsManager: AnalyticsManager,
    private val firebasePerformance: FirebasePerformance,
    private val geminiManager: GeminiManager
) : ViewModel() {

    // Session cards
    private val _sessionCards = MutableStateFlow<List<VocabularyCard>>(emptyList())
    val sessionCards: StateFlow<List<VocabularyCard>> = _sessionCards.asStateFlow()
    
    private var currentCardIndex = 0
    private var sessionStartTime = 0L
    
    // Review tracking
    private var correctCount = 0
    private var incorrectCount = 0
    private var newCardsLearned = 0
    
    // UI State
    private val _currentCard = MutableStateFlow<VocabularyCard?>(null)
    val currentCard: StateFlow<VocabularyCard?> = _currentCard.asStateFlow()
    
    private val _sessionStats = MutableStateFlow(SessionStats.empty())
    val sessionStats: StateFlow<SessionStats> = _sessionStats.asStateFlow()
    
    private val _isSessionComplete = MutableStateFlow(false)
    val isSessionComplete: StateFlow<Boolean> = _isSessionComplete.asStateFlow()
    
    private val _sessionProgress = MutableStateFlow(SessionProgress.empty())
    val sessionProgress: StateFlow<SessionProgress> = _sessionProgress.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isRatingEnabled = MutableStateFlow(true)
    val isRatingEnabled: StateFlow<Boolean> = _isRatingEnabled.asStateFlow()
    
    private val _noCardsAvailable = MutableStateFlow(false)
    val noCardsAvailable: StateFlow<Boolean> = _noCardsAvailable.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _aiExplanation = MutableStateFlow<VocabularyExplanation?>(null)
    val aiExplanation: StateFlow<VocabularyExplanation?> = _aiExplanation.asStateFlow()
    
    private val _isLoadingExplanation = MutableStateFlow(false)
    val isLoadingExplanation: StateFlow<Boolean> = _isLoadingExplanation.asStateFlow()

    private val preloadedExplanations = mutableMapOf<String, VocabularyExplanation>()


    init {
        initSession()
    }

    /**
     * Initialize a new practice session
     * Loads 70% review cards + 30% new cards (max 10 new cards per day)
     */
    fun initSession() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _noCardsAvailable.value = false
                _errorMessage.value = null
                
                val trace = firebasePerformance.newTrace("session_load_time")
                trace.start()
                
                sessionStartTime = System.currentTimeMillis()
                analyticsManager.logSessionStarted(SESSION_TYPE_VOCABULARY, "normal") // TODO: Get actual difficulty if relevant
                
                currentCardIndex = 0
                correctCount = 0
                incorrectCount = 0
                newCardsLearned = 0
                
                // Calculate card distribution: 70% review, 30% new
                // Miller's Law: Enforce a "magic number" of 7 for new items to avoid cognitive overload
                // Calculate card distribution: 70% review, 30% new
                // Miller's Law: Enforce a "magic number" of 7 for new items to avoid cognitive overload
                val MILLERS_LAW_CAP = 7
                val totalTargetCards = Constants.DEFAULT_DUE_CARDS_LIMIT
                val newCardLimit = minOf(MILLERS_LAW_CAP, (totalTargetCards * 0.3).toInt())
                val reviewCardLimit = totalTargetCards - newCardLimit
                
                // Load review cards (due cards)
                val reviewCards = spacedRepetitionEngine.getDueCards(reviewCardLimit).first()
                
                // Load new cards
                val newCards = spacedRepetitionEngine.getNewCards(newCardLimit).first()
                
                // Combine and shuffle for variety
                val allCards = (reviewCards + newCards).shuffled()
                
                if (allCards.isEmpty()) {
                    _noCardsAvailable.value = true
                    _isLoading.value = false
                    return@launch
                }
                
                _sessionCards.value = allCards
                
                // Track how many are new for stats
                newCardsLearned = newCards.size
                
                // Get review queue count for summary
                val reviewQueueCount = vocabularyRepository.getReviewQueueCount()
                
                // Update progress
                _sessionProgress.value = SessionProgress(
                    current = 0,
                    total = allCards.size,
                    dayNumber = calculateDayNumber(),
                    targetDays = 30
                )
                
                // Update initial stats
                _sessionStats.value = SessionStats(
                    wordsReviewed = 0,
                    totalWords = allCards.size,
                    correctCount = 0,
                    incorrectCount = 0,
                    accuracy = 0f,
                    timeElapsedSeconds = 0L,
                    newCardsLearned = newCards.size,
                    wordsInReviewQueue = reviewQueueCount
                )
                
                // Set first card
                _currentCard.value = allCards.firstOrNull()
                _isLoading.value = false
                preloadNextCards()
                
                trace.stop()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load cards: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Review the current card with a quality rating
     * @param quality Rating from 0-5:
     *   0-2: Again (failed to recall)
     *   3: Hard (barely remembered)
     *   4: Good (recalled with effort)
     *   5: Easy (instant recall)
     */
    fun reviewCard(quality: Int) {
        val card = _currentCard.value ?: return
        
        // Optimistic UI: Update state immediately
        _isRatingEnabled.value = false
        
        viewModelScope.launch {
            try {
                // Perform SM-2 calculation
                val updatedCard = spacedRepetitionEngine.reviewCard(card, quality)
                
                // Track in analytics
                analyticsManager.logCardReviewed(quality, quality >= Constants.MIN_QUALITY_TO_CONTINUE)
                
                // Update statistics (local state update)
                if (quality >= Constants.MIN_QUALITY_TO_CONTINUE) {
                    correctCount++
                } else {
                    incorrectCount++
                }
                
                val wordsReviewed = correctCount + incorrectCount
                val accuracy = if (wordsReviewed > 0) {
                    (correctCount.toFloat() / wordsReviewed) * 100
                } else 0f
                
                val timeElapsed = (System.currentTimeMillis() - sessionStartTime) / 1000
                
                _sessionStats.value = _sessionStats.value.copy(
                    wordsReviewed = wordsReviewed,
                    correctCount = correctCount,
                    incorrectCount = incorrectCount,
                    accuracy = accuracy,
                    timeElapsedSeconds = timeElapsed
                )
                
                // Update progress
                _sessionProgress.value = _sessionProgress.value.copy(
                    current = wordsReviewed
                )
                
                // Asynchronous DB save (Optimistic)
                launch {
                    try {
                        vocabularyRepository.updateCard(updatedCard)
                    } catch (e: Exception) {
                        // In a real app, we might handle rollback or retry, 
                        // but for now we'll just log or show a silent error
                        _errorMessage.value = "Sync failed, but progress is saved locally."
                    }
                }
                
                // Reduced delay for "Elite" feel (200ms instead of 500ms)
                // Just enough for the user to register the selection color
                delay(200L)
                advanceToNextCard()
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to process review: ${e.message}"
                _isRatingEnabled.value = true
            }
        }
    }

    /**
     * Skip the current card without reviewing
     */
    fun skipCard() {
        advanceToNextCard()
    }

    /**
     * End the session early
     */
    fun endSession() {
        viewModelScope.launch {
            saveSessionToDatabase()
            _isSessionComplete.value = true
        }
    }

    /**
     * Advance to the next card or complete session
     */
    private fun advanceToNextCard() {
        currentCardIndex++
        
        if (currentCardIndex >= _sessionCards.value.size) {
            // Session complete
            viewModelScope.launch {
                updateFinalStats()
                saveSessionToDatabase()
                val stats = _sessionStats.value
                analyticsManager.logSessionCompleted(stats.wordsReviewed, stats.accuracy, stats.timeElapsedSeconds)
                _isSessionComplete.value = true
            }
        } else {
            _currentCard.value = _sessionCards.value[currentCardIndex]
            _isRatingEnabled.value = true
            preloadNextCards()
        }
    }

    /**
     * Update final session statistics
     */
    private suspend fun updateFinalStats() {
        val timeElapsed = (System.currentTimeMillis() - sessionStartTime) / 1000
        val reviewQueueCount = try {
            vocabularyRepository.getReviewQueueCount()
        } catch (e: Exception) {
            0
        }
        
        _sessionStats.value = _sessionStats.value.copy(
            timeElapsedSeconds = timeElapsed,
            wordsInReviewQueue = reviewQueueCount
        )
    }

    /**
     * Save the completed session to the database
     */
    private suspend fun saveSessionToDatabase() {
        try {
            val stats = _sessionStats.value
            val currentUser = authRepository.getCurrentUser().first()
            val userId = currentUser?.id ?: ""
            
            val session = StudySession(
                userId = userId,
                startTime = sessionStartTime,
                endTime = System.currentTimeMillis(),
                durationMinutes = (stats.timeElapsedSeconds / 60).toInt(),
                sessionType = SESSION_TYPE_VOCABULARY,
                wordsReviewed = stats.wordsReviewed,
                accuracyPercentage = stats.accuracy,
                synced = false,
                createdAt = System.currentTimeMillis()
            )
            vocabularyRepository.saveStudySession(session)
        } catch (e: Exception) {
            _errorMessage.value = "Failed to save session: ${e.message}"
        }
    }

    /**
     * Clear any error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Get AI-powered explanation for the current card
     * Triggered when user rates a word as "Hard" (quality 3) or fails (quality < 3)
     */
    fun getAiExplanation() {
        val card = _currentCard.value ?: return
        
        viewModelScope.launch {
            try {
                _isLoadingExplanation.value = true
                _aiExplanation.value = null
                
                // Check if already preloaded
                preloadedExplanations[card.id]?.let {
                    _aiExplanation.value = it
                    _isLoadingExplanation.value = false
                    return@launch
                }

                val explanation = geminiManager.generateVocabularyExplanation(
                    word = card.word,
                    translation = card.translation,
                    difficulty = card.difficulty
                )
                
                _aiExplanation.value = explanation
                _isLoadingExplanation.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to get AI explanation: ${e.message}"
                _isLoadingExplanation.value = false
            }
        }
    }
    
    /**
     * Preload AI explanations for the next few cards
     */
    private fun preloadNextCards() {
        val nextIndices = (currentCardIndex + 1)..(currentCardIndex + PRELOAD_LIMIT)
        val cards = _sessionCards.value
        
        viewModelScope.launch {
            nextIndices.forEach { index ->
                if (index < cards.size) {
                    val card = cards[index]
                    if (!preloadedExplanations.containsKey(card.id)) {
                        try {
                            val explanation = geminiManager.generateVocabularyExplanation(
                                word = card.word,
                                translation = card.translation,
                                difficulty = card.difficulty
                            )
                            preloadedExplanations[card.id] = explanation
                        } catch (e: Exception) {
                            // Silent fail for preloading
                        }
                    }
                }
            }
        }
    }

    /**
     * Clear AI explanation when moving to next card
     */
    fun clearAiExplanation() {
        _aiExplanation.value = null
    }

    private suspend fun calculateDayNumber(): Int {
        val user = authRepository.getCurrentUser().first()
        val createdAt = user?.createdAt ?: System.currentTimeMillis()
        val msDiff = System.currentTimeMillis() - createdAt
        return (msDiff / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    companion object {
        private const val AUTO_ADVANCE_DELAY_MS = 500L
        private const val SESSION_TYPE_VOCABULARY = "vocabulary"
        private const val PRELOAD_LIMIT = 2
    }
}
