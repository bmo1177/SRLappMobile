package com.example.srlappexperiment.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * Data class representing daily study statistics
 */
@Immutable
data class DailyStat(
    val date: String, // YYYY-MM-DD
    val minutesStudied: Int
)

/**
 * Data class for retention rate trend points
 */
@Immutable
data class RetentionPoint(
    val date: String,
    val retention: Float // 0.0 - 1.0 (accuracy percentage)
)

/**
 * Data class for GitHub-style heatmap cells
 */
@Immutable
data class HeatmapCell(
    val date: String,
    val count: Int,
    val level: Int // 0-4
)

/**
 * Comprehensive dashboard stats model
 */
@Stable
data class DashboardStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalWordsLearned: Int = 0,
    val averageAccuracy: Float = 0f,
    val totalStudyHours: Float = 0f,
    val dueCardsCount: Int = 0,
    val newCardsCount: Int = 0,
    val targetScore: Int? = null,
    val currentProgress: Int? = null,
    val dailyStats: List<DailyStat> = emptyList(),
    val retentionTrend: List<RetentionPoint> = emptyList(),
    val heatmapData: List<HeatmapCell> = emptyList()
)
