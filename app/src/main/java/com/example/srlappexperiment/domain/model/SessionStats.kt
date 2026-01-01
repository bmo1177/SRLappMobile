package com.example.srlappexperiment.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * Statistics for a completed or ongoing vocabulary practice session
 */
@Stable
data class SessionStats(
    val wordsReviewed: Int = 0,
    val totalWords: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val accuracy: Float = 0f,
    val timeElapsedSeconds: Long = 0L,
    val newCardsLearned: Int = 0,
    val wordsInReviewQueue: Int = 0
) {
    companion object {
        fun empty() = SessionStats()
    }
}

/**
 * Progress tracking for the current practice session
 */
@Immutable
data class SessionProgress(
    val current: Int = 0,
    val total: Int = 0,
    val dayNumber: Int = 1,
    val targetDays: Int = 30
) {
    val progressFraction: Float
        get() = if (total > 0) current.toFloat() / total else 0f
    
    val isComplete: Boolean
        get() = current >= total && total > 0
    
    companion object {
        fun empty() = SessionProgress()
    }
}
