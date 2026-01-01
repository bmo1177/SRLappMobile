package com.example.srlappexperiment.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.srlappexperiment.domain.model.NotificationSettings
import com.example.srlappexperiment.domain.model.UserPreferences
import com.example.srlappexperiment.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    private object PreferencesKeys {
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val DAILY_GOAL = intPreferencesKey("daily_goal")
        val NEW_CARDS = intPreferencesKey("new_cards")
        val REVIEW_LIMIT = intPreferencesKey("review_limit")
        val DIFFICULTY = stringPreferencesKey("difficulty")
        val TARGET_SCORE = intPreferencesKey("target_score")
        val EXAM_DATE = longPreferencesKey("exam_date")
        val USER_TYPE = stringPreferencesKey("user_type")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SYSTEM_THEME = booleanPreferencesKey("system_theme")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val ONBOARDING_STEP = stringPreferencesKey("onboarding_step")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val TARGET_LANGUAGE = stringPreferencesKey("target_language")
        
        val NOTIF_ENABLED = booleanPreferencesKey("notif_enabled")
        val QUIET_START = stringPreferencesKey("quiet_start")
        val QUIET_END = stringPreferencesKey("quiet_end")
        val NOTIF_FREQ = stringPreferencesKey("notif_freq")
    }

    override val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            UserPreferences(
                displayName = preferences[PreferencesKeys.DISPLAY_NAME] ?: "",
                dailyGoalMinutes = preferences[PreferencesKeys.DAILY_GOAL] ?: 30,
                newCardsPerDay = preferences[PreferencesKeys.NEW_CARDS] ?: 10,
                reviewLimit = preferences[PreferencesKeys.REVIEW_LIMIT] ?: 30,
                difficultyLevel = preferences[PreferencesKeys.DIFFICULTY] ?: "intermediate",
                targetScore = preferences[PreferencesKeys.TARGET_SCORE],
                examDate = preferences[PreferencesKeys.EXAM_DATE],
                userType = preferences[PreferencesKeys.USER_TYPE] ?: "general",
                isDarkMode = preferences[PreferencesKeys.DARK_MODE] ?: false,
                useSystemTheme = preferences[PreferencesKeys.SYSTEM_THEME] ?: true,
                isOnboarded = preferences[PreferencesKeys.IS_ONBOARDED] ?: false,
                onboardingStep = preferences[PreferencesKeys.ONBOARDING_STEP] ?: "WELCOME",
                isPremium = preferences[PreferencesKeys.IS_PREMIUM] ?: false,
                targetLanguage = preferences[PreferencesKeys.TARGET_LANGUAGE] ?: "English"
            )
        }

    override val notificationSettings: Flow<NotificationSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            NotificationSettings(
                enabled = preferences[PreferencesKeys.NOTIF_ENABLED] ?: true,
                quietHoursStart = preferences[PreferencesKeys.QUIET_START] ?: "22:00",
                quietHoursEnd = preferences[PreferencesKeys.QUIET_END] ?: "08:00",
                frequency = preferences[PreferencesKeys.NOTIF_FREQ] ?: "daily"
            )
        }

    override suspend fun updatePreferences(prefs: UserPreferences) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DISPLAY_NAME] = prefs.displayName
            preferences[PreferencesKeys.DAILY_GOAL] = prefs.dailyGoalMinutes
            preferences[PreferencesKeys.NEW_CARDS] = prefs.newCardsPerDay
            preferences[PreferencesKeys.REVIEW_LIMIT] = prefs.reviewLimit
            preferences[PreferencesKeys.DIFFICULTY] = prefs.difficultyLevel
            prefs.targetScore?.let { preferences[PreferencesKeys.TARGET_SCORE] = it }
            prefs.examDate?.let { preferences[PreferencesKeys.EXAM_DATE] = it }
            preferences[PreferencesKeys.USER_TYPE] = prefs.userType
            preferences[PreferencesKeys.DARK_MODE] = prefs.isDarkMode
            preferences[PreferencesKeys.SYSTEM_THEME] = prefs.useSystemTheme
            preferences[PreferencesKeys.IS_ONBOARDED] = prefs.isOnboarded
            preferences[PreferencesKeys.ONBOARDING_STEP] = prefs.onboardingStep
            preferences[PreferencesKeys.IS_PREMIUM] = prefs.isPremium
            preferences[PreferencesKeys.TARGET_LANGUAGE] = prefs.targetLanguage
        }
    }

    override suspend fun updateNotificationSettings(settings: NotificationSettings) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIF_ENABLED] = settings.enabled
            preferences[PreferencesKeys.QUIET_START] = settings.quietHoursStart
            preferences[PreferencesKeys.QUIET_END] = settings.quietHoursEnd
            preferences[PreferencesKeys.NOTIF_FREQ] = settings.frequency
        }
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    override suspend fun setUseSystemTheme(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SYSTEM_THEME] = enabled
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
