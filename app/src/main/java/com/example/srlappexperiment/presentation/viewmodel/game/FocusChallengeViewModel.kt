package com.example.srlappexperiment.presentation.viewmodel.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.srlappexperiment.data.local.database.entities.GameResult
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.domain.repository.GameRepository
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import com.example.srlappexperiment.domain.usecase.SpacedRepetitionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
import com.example.srlappexperiment.data.analytics.AnalyticsManager
import com.example.srlappexperiment.data.remote.ai.GeminiManager

data class Question(
    val word: String,
    val options: List<String>,
    val correctIndex: Int,
    val pronunciation: String? = null,
    val originalCard: VocabularyCard
)

@HiltViewModel
class FocusChallengeViewModel @Inject constructor(
    private val spaceRepEngine: SpacedRepetitionEngine,
    private val repository: VocabularyRepository,
    private val gameRepository: GameRepository,
    private val analyticsManager: AnalyticsManager,
    private val geminiManager: GeminiManager
) : ViewModel() {

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion.asStateFlow()

    private val _timeRemaining = MutableStateFlow(90)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _accuracy = MutableStateFlow(0f)
    val accuracy: StateFlow<Float> = _accuracy.asStateFlow()

    private val _combo = MutableStateFlow(0)
    val combo: StateFlow<Int> = _combo.asStateFlow()

    private val _maxCombo = MutableStateFlow(0)
    val maxCombo: StateFlow<Int> = _maxCombo.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    private val _gameResult = MutableStateFlow<GameResult?>(null)
    val gameResult: StateFlow<GameResult?> = _gameResult.asStateFlow()

    private val _personalBest = MutableStateFlow(0)
    val personalBest: StateFlow<Int> = _personalBest.asStateFlow()

    private var timerJob: Job? = null
    private var allCards = listOf<VocabularyCard>()
    private var questionsAnswered = 0
    private var correctAnswers = 0
    private var currentDifficulty = "beginner"

    init {
        viewModelScope.launch {
            repository.getAllCards().collect {
                allCards = it
            }
        }
    }

    fun startGame(difficulty: String) {
        currentDifficulty = difficulty
        _score.value = 0
        _accuracy.value = 0f
        _combo.value = 0
        _maxCombo.value = 0
        _timeRemaining.value = 90
        _isGameOver.value = false
        _gameResult.value = null
        questionsAnswered = 0
        correctAnswers = 0

        _personalBest.value = 0 // Reset
        viewModelScope.launch {
            gameRepository.getPersonalBest(difficulty).take(1).collect {
                _personalBest.value = it ?: 0
            }
        }

        analyticsManager.logGameStarted(difficulty)
        generateNextQuestion()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0) {
                delay(1000)
                _timeRemaining.value -= 1
            }
            endGame()
        }
    }

    private fun generateNextQuestion() {
        if (allCards.size < 4) return

        val segmentCards = allCards.filter { 
            it.difficulty.equals(currentDifficulty, ignoreCase = true) 
        }.ifEmpty { allCards }

        val correctCard = segmentCards.random()
        val distractors = allCards.filter { it.id != correctCard.id }
            .shuffled()
            .take(3)

        val options = (distractors + correctCard).shuffled()
        val correctIndex = options.indexOf(correctCard)

        _currentQuestion.value = Question(
            word = correctCard.word,
            options = options.map { it.translation },
            correctIndex = correctIndex,
            pronunciation = correctCard.pronunciation,
            originalCard = correctCard
        )
        
        // Try to enhance with AI distractors asynchronously (doesn't block gameplay)
        viewModelScope.launch {
            try {
                val aiDistractors = geminiManager.generateQuizDistractors(
                    correctWord = correctCard.word,
                    correctTranslation = correctCard.translation,
                    existingWords = allCards.map { it.translation }
                )
                
                // Only update if we still have the same question
                if (_currentQuestion.value?.word == correctCard.word && aiDistractors.size >= 3) {
                    val enhancedOptions = (aiDistractors + correctCard.translation).shuffled()
                    val newCorrectIndex = enhancedOptions.indexOf(correctCard.translation)
                    
                    _currentQuestion.value = Question(
                        word = correctCard.word,
                        options = enhancedOptions,
                        correctIndex = newCorrectIndex,
                        pronunciation = correctCard.pronunciation,
                        originalCard = correctCard
                    )
                    
                    analyticsManager.logAiFeatureUsed("quiz_distractors")
                }
            } catch (e: Exception) {
                // Silently fail - we already have fallback distractors
            }
        }
    }

    fun selectAnswer(optionIndex: Int) {
        val current = _currentQuestion.value ?: return
        questionsAnswered++

        if (optionIndex == current.correctIndex) {
            correctAnswers++
            _combo.value += 1
            if (_combo.value > _maxCombo.value) _maxCombo.value = _combo.value
            
            // Scoring: +10 base, x2 if combo >= 5
            val points = if (_combo.value >= 5) 20 else 10
            _score.value += points
            
            // In a real app, we'd also update the card's SM-2 stats
            // viewModelScope.launch { spaceRepEngine.markCardReviewed(current.originalCard.id, 5) }
        } else {
            _combo.value = 0
            _score.value = (_score.value - 5).coerceAtLeast(0)
        }

        _accuracy.value = (correctAnswers.toFloat() / questionsAnswered.toFloat()) * 100f
        
        // Pacing based on difficulty (simulated briefly with a short delay before next)
        viewModelScope.launch {
            delay(300) // Brief visual feedback delay
            generateNextQuestion()
        }
    }

    fun endGame() {
        timerJob?.cancel()
        _isGameOver.value = true
        
        val result = GameResult(
            score = _score.value,
            accuracy = _accuracy.value,
            questionsAnswered = questionsAnswered,
            correctAnswers = correctAnswers,
            comboMax = _maxCombo.value,
            difficulty = currentDifficulty,
            timestamp = System.currentTimeMillis()
        )
        
        _gameResult.value = result
        analyticsManager.logGameCompleted(result.score, result.accuracy)
        
        viewModelScope.launch {
            gameRepository.saveGameResult(result)
        }
    }
}
