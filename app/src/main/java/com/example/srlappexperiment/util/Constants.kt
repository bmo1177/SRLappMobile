package com.example.srlappexperiment.util

object Constants {
    // Database
    const val DATABASE_NAME = "srl_app_database"
    const val DATABASE_VERSION = 2

    // DataStore
    const val DATASTORE_NAME = "srl_app_preferences"

    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_VOCABULARY = "vocabulary"
    const val COLLECTION_STUDY_SESSIONS = "study_sessions"
    const val COLLECTION_NOTIFICATIONS = "notifications"

    // Preferences Keys
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_IS_ONBOARDED = "is_onboarded"
    const val PREF_STREAK_COUNT = "streak_count"
    const val PREF_LAST_STUDY_DATE = "last_study_date"
    const val PREF_NOTIFICATION_ENABLED = "notification_enabled"
    
    // Onboarding Keys
    const val PREF_ONBOARDING_STEP = "onboarding_step"
    const val PREF_ONBOARDING_USER_TYPE = "onboarding_user_type"
    const val PREF_ONBOARDING_TARGET_SCORE = "onboarding_target_score"
    const val PREF_ONBOARDING_EXAM_DATE = "onboarding_exam_date"
    const val PREF_ONBOARDING_STUDY_TIME = "onboarding_study_time"
    
    // Goal Settings
    const val MIN_TARGET_SCORE = 1
    const val MAX_TARGET_SCORE = 9
    const val MIN_STUDY_TIME_MINUTES = 15
    const val MAX_STUDY_TIME_MINUTES = 60
    const val DEFAULT_STUDY_TIME_MINUTES = 30

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "study_reminder_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Study Reminders"

    // Study Session
    const val FOCUS_CHALLENGE_DURATION_SECONDS = 90L
    const val MIN_STUDY_DURATION_SECONDS = 300L // 5 minutes

    // Spaced Repetition (SM-2 Algorithm)
    const val INITIAL_EASINESS_FACTOR = 2.5f
    const val MIN_EASINESS_FACTOR = 1.3f
    const val FIRST_REVIEW_INTERVAL_DAYS = 1
    const val SECOND_REVIEW_INTERVAL_DAYS = 6
    const val MIN_QUALITY_RATING = 0
    const val MAX_QUALITY_RATING = 5
    const val MIN_QUALITY_TO_CONTINUE = 3 // Quality >= 3 to continue progression
    const val DEFAULT_DUE_CARDS_LIMIT = 30
    const val DEFAULT_NEW_CARDS_LIMIT = 10

    // Date Format
    const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
}

