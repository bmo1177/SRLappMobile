package com.example.srlappexperiment.domain.notifications

import com.example.srlappexperiment.data.local.database.entities.DailyProgress
import com.example.srlappexperiment.data.remote.ai.GeminiManager
import com.example.srlappexperiment.domain.model.EngagementLevel
import com.example.srlappexperiment.domain.model.NotificationTemplate
import com.example.srlappexperiment.domain.repository.NotificationTemplateRepository
import com.example.srlappexperiment.domain.repository.ProgressRepository
import com.example.srlappexperiment.domain.repository.StudySessionRepository
import com.example.srlappexperiment.domain.repository.UserRepository
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class NotificationPersonalizationEngineTest {

    @Mock lateinit var userRepository: UserRepository
    @Mock lateinit var sessionRepository: StudySessionRepository
    @Mock lateinit var progressRepository: ProgressRepository
    @Mock lateinit var templateRepository: NotificationTemplateRepository
    @Mock lateinit var vocabularyRepository: VocabularyRepository
    @Mock lateinit var geminiManager: GeminiManager

    private lateinit var engine: NotificationPersonalizationEngine

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        engine = NotificationPersonalizationEngine(
            userRepository,
            sessionRepository,
            progressRepository,
            templateRepository,
            vocabularyRepository,
            geminiManager
        )
    }

    @Test
    fun `determineEngagementLevel should return HIGH when active 5 days`() = runTest {
        // DailyProgress(date, streak, minutes, reviewed, learned, accuracy)
        val progress = listOf(
            DailyProgress("2023-10-01", 0, 10),
            DailyProgress("2023-10-02", 0, 10),
            DailyProgress("2023-10-03", 0, 10),
            DailyProgress("2023-10-04", 0, 10),
            DailyProgress("2023-10-05", 0, 10),
            DailyProgress("2023-10-06", 0, 0),
            DailyProgress("2023-10-07", 0, 0)
        )
        `when`(progressRepository.getProgressRange(anyString(), anyString())).thenReturn(flowOf(progress))

        val level = engine.determineEngagementLevel("user123").first()
        assertEquals(EngagementLevel.HIGH, level)
    }

    @Test
    fun `selectOptimalNotification should choose highest CTR template`() = runTest {
        // Should default to LOW because progress mock remains empty (0 active days)
        val templates = listOf(
            NotificationTemplate("1", "Low CTR", EngagementLevel.LOW, mapOf(EngagementLevel.LOW to 0.1f)),
            NotificationTemplate("2", "High CTR", EngagementLevel.LOW, mapOf(EngagementLevel.LOW to 0.5f))
        )
        
        `when`(progressRepository.getProgressRange(anyString(), anyString())).thenReturn(flowOf(emptyList()))
        `when`(templateRepository.getTemplatesForSegment(EngagementLevel.LOW)).thenReturn(flowOf(templates))

        val selected = engine.selectOptimalNotification("user123").first()
        assertEquals("2", selected?.id)
    }

    private fun anyString(): String = org.mockito.ArgumentMatchers.anyString()
}
