package com.example.srlappexperiment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.srlappexperiment.domain.model.*
import com.example.srlappexperiment.domain.repository.ProgressRepository
import com.example.srlappexperiment.domain.repository.StudySessionRepository
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import com.example.srlappexperiment.data.sync.SyncEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import com.example.srlappexperiment.data.analytics.AnalyticsManager

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val vocabularyRepository: VocabularyRepository,
    private val studySessionRepository: StudySessionRepository,
    private val progressRepository: ProgressRepository,
    private val syncEngine: SyncEngine,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    
    val syncStatus: StateFlow<SyncStatus> = syncEngine.syncStatus

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val dashboardStats: StateFlow<DashboardStats> = combine(
        progressRepository.getCurrentStreak().catch { emit(0) },
        progressRepository.getLongestStreak().catch { emit(0) },
        vocabularyRepository.getTotalCardsLearned().catch { emit(0) },
        studySessionRepository.getAverageAccuracy().catch { emit(0f) },
        studySessionRepository.getTotalStudyTimeMinutes().catch { emit(0) },
        vocabularyRepository.getDueCards(Int.MAX_VALUE).catch { emit(emptyList<com.example.srlappexperiment.data.local.database.entities.VocabularyCard>()) },
        vocabularyRepository.getNewCards(Int.MAX_VALUE).catch { emit(emptyList<com.example.srlappexperiment.data.local.database.entities.VocabularyCard>()) },
        progressRepository.getProgressRange(getPastDate(30), getCurrentDate()).catch { emit(emptyList<com.example.srlappexperiment.data.local.database.entities.DailyProgress>()) }
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        try {
            val currentStreak = (args[0] as? Int) ?: 0
            val longestStreak = (args[1] as? Int) ?: 0
            val learned = (args[2] as? Int) ?: 0
            val accuracy = (args[3] as? Float) ?: 0f
            val minutes = (args[4] as? Int) ?: 0
            val due = (args[5] as? List<com.example.srlappexperiment.data.local.database.entities.VocabularyCard>) ?: emptyList()
            val new = (args[6] as? List<com.example.srlappexperiment.data.local.database.entities.VocabularyCard>) ?: emptyList()
            val recentProgress = (args[7] as? List<com.example.srlappexperiment.data.local.database.entities.DailyProgress>) ?: emptyList()

            DashboardStats(
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                totalWordsLearned = learned,
                averageAccuracy = accuracy,
                totalStudyHours = minutes / 60f,
                dueCardsCount = due.size,
                newCardsCount = new.size,
                dailyStats = recentProgress.map { DailyStat(it.date, it.minutesStudied) }.reversed(),
                retentionTrend = calculateRetentionTrend(recentProgress),
                heatmapData = calculateHeatmapData(recentProgress)
            ).also { stats ->
                try {
                    // Log streak updates or goal achievements if needed
                    analyticsManager.logStreakUpdated(stats.currentStreak)
                    if (stats.dailyStats.any { it.minutesStudied >= 30 }) {
                        analyticsManager.logDailyGoalAchieved()
                    }
                } catch (e: Exception) {
                    // Ignore analytics errors
                }
            }
        } catch (e: Exception) {
            // Return default stats on error
            DashboardStats()
        }
    }.onStart { _isLoading.value = false }
    .catch { e ->
        _isLoading.value = false
        emit(DashboardStats())
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardStats()
    )

    fun triggerSync() {
        viewModelScope.launch {
            syncEngine.syncAll()
        }
    }

    fun refreshStats() {
        // Stats are automatically refreshed via StateFlow combine
    }

    private fun calculateRetentionTrend(progress: List<com.example.srlappexperiment.data.local.database.entities.DailyProgress>): List<RetentionPoint> {
        return progress.map { 
            RetentionPoint(it.date, it.accuracyPercentage / 100f) 
        }.reversed()
    }

    private fun calculateHeatmapData(progress: List<com.example.srlappexperiment.data.local.database.entities.DailyProgress>): List<HeatmapCell> {
        return progress.map {
            val level = when {
                it.minutesStudied == 0 -> 0
                it.minutesStudied < 15 -> 1
                it.minutesStudied < 30 -> 2
                it.minutesStudied < 60 -> 3
                else -> 4
            }
            HeatmapCell(it.date, it.minutesStudied, level)
        }
    }

    private fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    private fun getPastDate(daysAgo: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return dateFormat.format(cal.time)
    }
}
