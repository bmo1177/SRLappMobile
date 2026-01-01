package com.example.srlappexperiment.domain.model

import java.util.Date

/**
 * Notification model for AI-powered notification system
 */
data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: NotificationType = NotificationType.STUDY_REMINDER,
    val scheduledTime: Long = Date().time,
    val isSent: Boolean = false,
    val isRead: Boolean = false,
    val priority: Int = 0, // For bandit algorithm scoring
    val createdAt: Long = Date().time
)

enum class NotificationType {
    STUDY_REMINDER,
    STREAK_REMINDER,
    ACHIEVEMENT,
    DAILY_CHALLENGE
}

