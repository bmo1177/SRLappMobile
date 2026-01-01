package com.example.srlappexperiment.domain.repository

import com.example.srlappexperiment.domain.model.NotificationSettings
import com.example.srlappexperiment.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app-specific preferences and configuration
 */
interface PreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    val notificationSettings: Flow<NotificationSettings>
    
    suspend fun updatePreferences(prefs: UserPreferences)
    suspend fun updateNotificationSettings(settings: NotificationSettings)
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setUseSystemTheme(enabled: Boolean)
    suspend fun clearAll()
}
