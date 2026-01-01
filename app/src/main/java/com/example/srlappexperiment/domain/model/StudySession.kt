package com.example.srlappexperiment.domain.model

import androidx.compose.runtime.Stable

/**
 * Domain model for StudySession
 * Note: Room entity is in data.local.database.entities.StudySession
 */
@Stable
data class StudySession(
    val id: String = "",
    val userId: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val durationSeconds: Long = 0,
    val cardsStudied: Int = 0,
    val cardsCorrect: Int = 0,
    val cardsIncorrect: Int = 0,
    val sessionType: SessionType = SessionType.VOCABULARY,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class SessionType {
    VOCABULARY,
    FOCUS_CHALLENGE,
    REVIEW
}

