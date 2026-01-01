package com.example.srlappexperiment.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * User domain model for authentication and profile
 * Maps to Firestore users collection
 */
@Stable
data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String? = null,
    val photoUrl: String? = null,
    val userType: String = "general", // ielts/toefl/general
    val targetScore: Int? = null,
    val examDate: Long? = null, // Timestamp
    val createdAt: Long = System.currentTimeMillis(),
    val lastActive: Long = System.currentTimeMillis(),
    val engagementLevel: String = "medium", // high/medium/low
    val streakCount: Int = 0,
    val lastStudyDate: Long? = null,
    val totalCardsStudied: Int = 0,
    val totalStudyTime: Long = 0, // Total study time in seconds
    val level: String? = null, // Assessed level (e.g., A1, B2)
    val isOnboarded: Boolean = false,
    val notificationPreferences: NotificationPreferences? = null
)

/**
 * Notification preferences for user
 */
@Immutable
data class NotificationPreferences(
    val enabled: Boolean = true,
    val quietHoursStart: String = "22:00", // HH:MM format
    val quietHoursEnd: String = "08:00", // HH:MM format
    val frequency: String = "daily" // daily/twice_daily
)

