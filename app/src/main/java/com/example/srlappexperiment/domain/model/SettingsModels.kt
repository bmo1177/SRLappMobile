package com.example.srlappexperiment.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * High-level user preferences for the application
 */
@Stable
data class UserPreferences(
    val displayName: String = "",
    val dailyGoalMinutes: Int = 30,
    val newCardsPerDay: Int = 10,
    val reviewLimit: Int = 30,
    val difficultyLevel: String = "intermediate",
    val targetScore: Int? = null,
    val examDate: Long? = null,
    val userType: String = "general",
    val isDarkMode: Boolean = false,
    val useSystemTheme: Boolean = true,
    val isOnboarded: Boolean = false,
    val onboardingStep: String = "WELCOME",
    val isPremium: Boolean = false,
    val targetLanguage: String = "English"
)

/**
 * Granular notification settings
 * Maps to User.notificationPreferences in Firestore
 */
@Immutable
data class NotificationSettings(
    val enabled: Boolean = true,
    val quietHoursStart: String = "22:00", // HH:MM
    val quietHoursEnd: String = "08:00",
    val frequency: String = "daily" // daily/twice_daily
)
