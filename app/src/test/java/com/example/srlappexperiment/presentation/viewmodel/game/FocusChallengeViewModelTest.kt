package com.example.srlappexperiment.presentation.viewmodel.game

import com.example.srlappexperiment.data.analytics.AnalyticsManager
import com.example.srlappexperiment.data.remote.ai.GeminiManager
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.domain.repository.GameRepository
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import com.example.srlappexperiment.domain.usecase.SpacedRepetitionEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class FocusChallengeViewModelTest {

    @Mock lateinit var engine: SpacedRepetitionEngine
    @Mock lateinit var vocabularyRepository: VocabularyRepository
    @Mock lateinit var gameRepository: GameRepository
    @Mock lateinit var analyticsManager: AnalyticsManager
    @Mock lateinit var geminiManager: GeminiManager

    private lateinit var viewModel: FocusChallengeViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val cards = listOf(
        VocabularyCard("1", "Hello", "Bonjour", difficulty = "beginner"),
        VocabularyCard("2", "World", "Monde", difficulty = "beginner"),
        VocabularyCard("3", "Apple", "Pomme", difficulty = "beginner"),
        VocabularyCard("4", "Banana", "Banane", difficulty = "beginner")
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        `when`(vocabularyRepository.getAllCards()).thenReturn(flowOf(cards))
        `when`(gameRepository.getPersonalBest("beginner")).thenReturn(flowOf(500))

        viewModel = FocusChallengeViewModel(engine, vocabularyRepository, gameRepository, analyticsManager, geminiManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startGame should initialize game state and first question`() = runTest {
        viewModel.startGame("beginner")

        assertEquals(0, viewModel.score.value)
        assertEquals(90, viewModel.timeRemaining.value)
        assertNotNull(viewModel.currentQuestion.value)
        assertEquals(500, viewModel.personalBest.value)
    }

    @Test
    fun `selectAnswer correct should increase score and combo`() = runTest {
        viewModel.startGame("beginner")

        val question = viewModel.currentQuestion.value!!
        viewModel.selectAnswer(question.correctIndex)
        
        assertEquals(10, viewModel.score.value)
        assertEquals(1, viewModel.combo.value)
    }

    @Test
    fun `selectAnswer incorrect should decrease score and reset combo`() = runTest {
        viewModel.startGame("beginner")

        val question = viewModel.currentQuestion.value!!
        val wrongIndex = (question.correctIndex + 1) % 4
        
        // Correct first
        viewModel.selectAnswer(question.correctIndex) 
        assertEquals(10, viewModel.score.value)
        
        // Then wrong
        viewModel.selectAnswer(wrongIndex)
        assertEquals(5, viewModel.score.value)
        assertEquals(0, viewModel.combo.value)
    }

    @Test
    fun `combo 5 should give 2x multiplier`() = runTest {
        viewModel.startGame("beginner")

        // 4 correct answers
        repeat(4) {
            val q = viewModel.currentQuestion.value!!
            viewModel.selectAnswer(q.correctIndex)
        }
        assertEquals(40, viewModel.score.value)
        assertEquals(4, viewModel.combo.value)

        // 5th correct answer
        val q5 = viewModel.currentQuestion.value!!
        viewModel.selectAnswer(q5.correctIndex)
        
        assertEquals(60, viewModel.score.value)
        assertEquals(5, viewModel.combo.value)
    }
}
