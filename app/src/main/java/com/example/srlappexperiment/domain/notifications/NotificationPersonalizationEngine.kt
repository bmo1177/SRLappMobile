package com.example.srlappexperiment.domain.notifications

import com.example.srlappexperiment.domain.model.EngagementLevel
import com.example.srlappexperiment.domain.model.NotificationTemplate
import com.example.srlappexperiment.domain.repository.ProgressRepository
import com.example.srlappexperiment.domain.repository.StudySessionRepository
import com.example.srlappexperiment.domain.repository.NotificationTemplateRepository
import com.example.srlappexperiment.domain.repository.UserRepository
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import com.example.srlappexperiment.data.remote.ai.GeminiManager

@Singleton
class NotificationPersonalizationEngine @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: StudySessionRepository,
    private val progressRepository: ProgressRepository,
    private val templateRepository: NotificationTemplateRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val geminiManager: GeminiManager
) {

    /**
     * Selects the optimal notification template for the user based on CTR and novelty.
     */
    fun selectOptimalNotification(userId: String): Flow<NotificationTemplate?> = flow {
        val engagementLevel = determineEngagementLevel(userId).first()
        
        val templates = templateRepository.getTemplatesForSegment(engagementLevel).first()
        if (templates.isEmpty()) {
            emit(null)
            return@flow
        }

        // Filter out recently used templates (within last 7 days)
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        val freshTemplates = templates.filter { 
            it.lastUsedTimestamp == null || it.lastUsedTimestamp < sevenDaysAgo 
        }

        val candidates = if (freshTemplates.isNotEmpty()) freshTemplates else templates

        // Bandit-style selection: highest CTR first, fallback to random
        val selected = candidates.maxByOrNull { it.getCtrForSegment(engagementLevel) }
        emit(selected)
    }

    /**
     * Determines the optimal time to send a notification (15 mins before peak hour).
     */
    fun getOptimalSendTime(userId: String): Flow<Long> = flow {
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        val sessions = sessionRepository.getSessionsInRange(thirtyDaysAgo, System.currentTimeMillis()).first()
        
        if (sessions.isEmpty()) {
            // Default to 9:00 AM if no history
            emit(getDefaultSendTime())
            return@flow
        }

        // Group by hour
        val hourCounts = sessions.map { 
            val cal = Calendar.getInstance().apply { timeInMillis = it.startTime }
            cal.get(Calendar.HOUR_OF_DAY)
        }.groupingBy { it }.eachCount()

        // Find top hour
        val peakHour = hourCounts.maxByOrNull { it.value }?.key ?: 9
        
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, peakHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            add(Calendar.MINUTE, -15)
            
            // If the time has already passed today, schedule for tomorrow
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            
            // Respect quiet hours (10 PM - 8 AM)
            val finalHour = get(Calendar.HOUR_OF_DAY)
            if (finalHour > 22 || finalHour < 8) {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
            }
        }

        emit(targetTime.timeInMillis)
    }

    /**
     * Calculates the engagement level based on active days in the last 7 days.
     */
    fun determineEngagementLevel(userId: String): Flow<EngagementLevel> = flow {
        val today = Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val endDate = dateFormat.format(today.time)
        today.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = dateFormat.format(today.time)

        val progress = progressRepository.getProgressRange(startDate, endDate).first()
        val activeDays = progress.filter { it.minutesStudied > 0 }.size

        val level = when {
            activeDays >= 5 -> EngagementLevel.HIGH
            activeDays >= 2 -> EngagementLevel.MEDIUM
            else -> EngagementLevel.LOW
        }
        
        userRepository.updateEngagementLevel(userId, level)
        emit(level)
    }

    fun recordNotificationClick(userId: String, templateId: String, segment: EngagementLevel): Flow<Unit> = flow {
        templateRepository.recordClick(templateId, segment)
        emit(Unit)
    }

    private fun getDefaultSendTime(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }.timeInMillis
    }
    
    /**
     * Generate AI-powered personalized notification message
     * @param userId User ID
     * @return Personalized, motivational notification message
     */
    suspend fun generatePersonalizedNotification(userId: String): String = flow {
        try {
            // Get user context
            val user = userRepository.getUser(userId).first() ?: return@flow
            val dueCardsCount = vocabularyRepository.getReviewQueueCount()
            val streakDays = progressRepository.getCurrentStreak().first()
            val engagementLevel = determineEngagementLevel(userId).first()
            
            // Generate with AI
            val message = geminiManager.generateMotivationalNotification(
                userName = user.displayName ?: "there",
                dueCardsCount = dueCardsCount,
                streakDays = streakDays,
                engagementLevel = engagementLevel.name
            )
            
            emit(message)
        } catch (e: Exception) {
            // Fallback to template-based
            emit("Time to practice! You've got this! 🎯")
        }
    }.first()
}
