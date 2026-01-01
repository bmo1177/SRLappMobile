package com.example.srlappexperiment.presentation.viewmodel

import com.example.srlappexperiment.data.analytics.AnalyticsManager
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.data.remote.ai.GeminiManager
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import com.example.srlappexperiment.domain.repository.AuthRepository
import com.example.srlappexperiment.domain.usecase.SpacedRepetitionEngine
import com.example.srlappexperiment.util.Constants
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for VocabularyPracticeViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VocabularyPracticeViewModelTest {

    private lateinit var viewModel: VocabularyPracticeViewModel
    private lateinit var mockSpacedRepetitionEngine: SpacedRepetitionEngine
    private lateinit var mockVocabularyRepository: VocabularyRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var mockAnalyticsManager: AnalyticsManager
    private lateinit var mockGeminiManager: GeminiManager
    private lateinit var mockFirebasePerformance: FirebasePerformance
    private lateinit var mockTrace: Trace
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockSpacedRepetitionEngine = mock()
        mockVocabularyRepository = mock()
        mockAuthRepository = mock()
        mockAnalyticsManager = mock()
        mockGeminiManager = mock()
        mockFirebasePerformance = mock()
        mockTrace = mock()
        
        whenever(mockFirebasePerformance.newTrace(any())).thenReturn(mockTrace)
        whenever(mockAuthRepository.getCurrentUser()).thenReturn(flowOf(null))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initSession loads mixed review and new cards`() = runTest {
        // Given: Mock returns review and new cards
        val reviewCards = listOf(
            createTestCard("review1", timesReviewed = 5),
            createTestCard("review2", timesReviewed = 3)
        )
        val newCards = listOf(
            createTestCard("new1", timesReviewed = 0),
            createTestCard("new2", timesReviewed = 0)
        )
        
        whenever(mockSpacedRepetitionEngine.getDueCards(any())).thenReturn(flowOf(reviewCards))
        whenever(mockSpacedRepetitionEngine.getNewCards(any())).thenReturn(flowOf(newCards))
        whenever(mockVocabularyRepository.getReviewQueueCount()).thenReturn(10)
        
        // When: ViewModel is created
        viewModel = VocabularyPracticeViewModel(
            spacedRepetitionEngine = mockSpacedRepetitionEngine,
            vocabularyRepository = mockVocabularyRepository,
            authRepository = mockAuthRepository,
            analyticsManager = mockAnalyticsManager,
            firebasePerformance = mockFirebasePerformance,
            geminiManager = mockGeminiManager
        )
        advanceUntilIdle()
        
        // Then: Session should be loaded with cards
        assertFalse("Should not be loading", viewModel.isLoading.value)
        assertFalse("Should have cards available", viewModel.noCardsAvailable.value)
        assertNotNull("Should have current card", viewModel.currentCard.value)
        assertEquals("Total should be 4", 4, viewModel.sessionProgress.value.total)
    }

    @Test
    fun `initSession shows noCardsAvailable when no cards exist`() = runTest {
        // Given: No cards available
        whenever(mockSpacedRepetitionEngine.getDueCards(any())).thenReturn(flowOf(emptyList()))
        whenever(mockSpacedRepetitionEngine.getNewCards(any())).thenReturn(flowOf(emptyList()))
        
        // When: ViewModel is created
        viewModel = VocabularyPracticeViewModel(
            spacedRepetitionEngine = mockSpacedRepetitionEngine,
            vocabularyRepository = mockVocabularyRepository,
            authRepository = mockAuthRepository,
            analyticsManager = mockAnalyticsManager,
            firebasePerformance = mockFirebasePerformance,
            geminiManager = mockGeminiManager
        )
        advanceUntilIdle()
        
        // Then: Should show no cards available
        assertFalse("Should not be loading", viewModel.isLoading.value)
        assertTrue("Should have no cards available", viewModel.noCardsAvailable.value)
    }

    @Test
    fun `reviewCard with good quality updates stats correctly`() = runTest {
        // Given: Session with cards
        val cards = listOf(createTestCard("card1"))
        whenever(mockSpacedRepetitionEngine.getDueCards(any())).thenReturn(flowOf(cards))
        whenever(mockSpacedRepetitionEngine.getNewCards(any())).thenReturn(flowOf(emptyList()))
        whenever(mockVocabularyRepository.getReviewQueueCount()).thenReturn(5)
        whenever(mockSpacedRepetitionEngine.reviewCard(any(), any())).thenReturn(cards.first())
        
        viewModel = VocabularyPracticeViewModel(
            spacedRepetitionEngine = mockSpacedRepetitionEngine,
            vocabularyRepository = mockVocabularyRepository,
            authRepository = mockAuthRepository,
            analyticsManager = mockAnalyticsManager,
            firebasePerformance = mockFirebasePerformance,
            geminiManager = mockGeminiManager
        )
        advanceUntilIdle()
        
        // When: Review card with quality 4 (Good)
        viewModel.reviewCard(Constants.MIN_QUALITY_TO_CONTINUE + 1) // Quality 4
        advanceUntilIdle()
        
        // Then: Stats should be updated
        val stats = viewModel.sessionStats.value
        assertEquals("Should have 1 word reviewed", 1, stats.wordsReviewed)
        assertEquals("Should have 1 correct", 1, stats.correctCount)
        assertEquals("Should have 0 incorrect", 0, stats.incorrectCount)
        assertEquals("Accuracy should be 100%", 100f, stats.accuracy, 0.1f)
    }

    @Test
    fun `reviewCard with poor quality marks as incorrect`() = runTest {
        // Given: Session with cards
        val cards = listOf(createTestCard("card1"), createTestCard("card2"))
        whenever(mockSpacedRepetitionEngine.getDueCards(any())).thenReturn(flowOf(cards))
        whenever(mockSpacedRepetitionEngine.getNewCards(any())).thenReturn(flowOf(emptyList()))
        whenever(mockVocabularyRepository.getReviewQueueCount()).thenReturn(5)
        whenever(mockSpacedRepetitionEngine.reviewCard(any(), any())).thenReturn(cards.first())
        
        viewModel = VocabularyPracticeViewModel(
            spacedRepetitionEngine = mockSpacedRepetitionEngine,
            vocabularyRepository = mockVocabularyRepository,
            authRepository = mockAuthRepository,
            analyticsManager = mockAnalyticsManager,
            firebasePerformance = mockFirebasePerformance,
            geminiManager = mockGeminiManager
        )
        advanceUntilIdle()
        
        // When: Review card with quality 1 (Again)
        viewModel.reviewCard(1) // Quality 1 = Again
        advanceUntilIdle()
        
        // Then: Stats should show incorrect
        val stats = viewModel.sessionStats.value
        assertEquals("Should have 1 word reviewed", 1, stats.wordsReviewed)
        assertEquals("Should have 0 correct", 0, stats.correctCount)
        assertEquals("Should have 1 incorrect", 1, stats.incorrectCount)
    }

    @Test
    fun `reviewCard saves updated card to repository`() = runTest {
        // Given: Session with cards
        val cards = listOf(createTestCard("card1"))
        val updatedCard = cards.first().copy(timesReviewed = 1)
        whenever(mockSpacedRepetitionEngine.getDueCards(any())).thenReturn(flowOf(cards))
        whenever(mockSpacedRepetitionEngine.getNewCards(any())).thenReturn(flowOf(emptyList()))
        whenever(mockVocabularyRepository.getReviewQueueCount()).thenReturn(5)
        whenever(mockSpacedRepetitionEngine.reviewCard(any(), any())).thenReturn(updatedCard)
        
        viewModel = VocabularyPracticeViewModel(
            spacedRepetitionEngine = mockSpacedRepetitionEngine,
            vocabularyRepository = mockVocabularyRepository,
            authRepository = mockAuthRepository,
            analyticsManager = mockAnalyticsManager,
            firebasePerformance = mockFirebasePerformance,
            geminiManager = mockGeminiManager
        )
        advanceUntilIdle()
        
        // When: Review card
        viewModel.reviewCard(4)
        advanceUntilIdle()
        
        // Then: Card should be saved to repository
        verify(mockVocabularyRepository).updateCard(updatedCard)
    }

    @Test
    fun `endSession saves session to database`() = runTest {
        // Given: Session with cards
        val cards = listOf(createTestCard("card1"))
        whenever(mockSpacedRepetitionEngine.getDueCards(any())).thenReturn(flowOf(cards))
        whenever(mockSpacedRepetitionEngine.getNewCards(any())).thenReturn(flowOf(emptyList()))
        whenever(mockVocabularyRepository.getReviewQueueCount()).thenReturn(5)
        whenever(mockVocabularyRepository.saveStudySession(any())).thenReturn(1L)
        
        viewModel = VocabularyPracticeViewModel(
            spacedRepetitionEngine = mockSpacedRepetitionEngine,
            vocabularyRepository = mockVocabularyRepository,
            authRepository = mockAuthRepository,
            analyticsManager = mockAnalyticsManager,
            firebasePerformance = mockFirebasePerformance,
            geminiManager = mockGeminiManager
        )
        advanceUntilIdle()
        
        // When: End session
        viewModel.endSession()
        advanceUntilIdle()
        
        // Then: Session should be saved and marked complete
        assertTrue("Session should be complete", viewModel.isSessionComplete.value)
        verify(mockVocabularyRepository).saveStudySession(any())
    }

    @Test
    fun `session completes after all cards reviewed`() = runTest {
        // Given: Session with single card
        val cards = listOf(createTestCard("card1"))
        whenever(mockSpacedRepetitionEngine.getDueCards(any())).thenReturn(flowOf(cards))
        whenever(mockSpacedRepetitionEngine.getNewCards(any())).thenReturn(flowOf(emptyList()))
        whenever(mockVocabularyRepository.getReviewQueueCount()).thenReturn(0)
        whenever(mockSpacedRepetitionEngine.reviewCard(any(), any())).thenReturn(cards.first())
        whenever(mockVocabularyRepository.saveStudySession(any())).thenReturn(1L)
        
        viewModel = VocabularyPracticeViewModel(
            spacedRepetitionEngine = mockSpacedRepetitionEngine,
            vocabularyRepository = mockVocabularyRepository,
            authRepository = mockAuthRepository,
            analyticsManager = mockAnalyticsManager,
            firebasePerformance = mockFirebasePerformance,
            geminiManager = mockGeminiManager
        )
        advanceUntilIdle()
        
        // When: Review the only card
        viewModel.reviewCard(4)
        advanceUntilIdle()
        
        // Then: Session should be complete
        assertTrue("Session should be complete after last card", viewModel.isSessionComplete.value)
    }

    // Helper function to create test cards
    private fun createTestCard(
        id: String = "test-card",
        timesReviewed: Int = 0
    ): VocabularyCard {
        return VocabularyCard(
            id = id,
            word = "test word",
            translation = "test translation",
            definition = "test definition",
            exampleSentence = "This is a test sentence.",
            pronunciation = "test",
            difficulty = "intermediate",
            category = "ielts_academic",
            easeFactor = 2.5f,
            interval = 1,
            nextReviewDate = System.currentTimeMillis(),
            lastReviewDate = null,
            timesReviewed = timesReviewed,
            timesCorrect = 0,
            timesIncorrect = 0,
            synced = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}
