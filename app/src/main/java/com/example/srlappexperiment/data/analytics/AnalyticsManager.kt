package com.example.srlappexperiment.data.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    private val analytics: FirebaseAnalytics
) {
    // User Onboarding
    fun logOnboardingStarted() {
        analytics.logEvent("onboarding_started", null)
    }

    fun logOnboardingCompleted() {
        analytics.logEvent("onboarding_completed", null)
    }

    fun logOnboardingSkipped() {
        analytics.logEvent("onboarding_skipped", null)
    }

    // Vocabulary Practice
    fun logSessionStarted(sessionType: String, difficulty: String) {
        val bundle = Bundle().apply {
            putString("session_type", sessionType)
            putString("difficulty", difficulty)
        }
        analytics.logEvent("session_started", bundle)
    }

    fun logSessionCompleted(wordsReviewed: Int, accuracy: Float, durationSeconds: Long) {
        val bundle = Bundle().apply {
            putInt("words_reviewed", wordsReviewed)
            putFloat("accuracy", accuracy)
            putLong("duration_seconds", durationSeconds)
        }
        analytics.logEvent("session_completed", bundle)
    }

    fun logCardReviewed(quality: Int, accuracy: Boolean) {
        val bundle = Bundle().apply {
            putInt("quality", quality)
            putBoolean("accuracy", accuracy)
        }
        analytics.logEvent("card_reviewed", bundle)
    }

    // Gamification
    fun logGameStarted(difficulty: String) {
        val bundle = Bundle().apply {
            putString("difficulty", difficulty)
        }
        analytics.logEvent("game_started", bundle)
    }

    fun logGameCompleted(score: Int, accuracy: Float) {
        val bundle = Bundle().apply {
            putInt("score", score)
            putFloat("accuracy", accuracy)
        }
        analytics.logEvent("game_completed", bundle)
    }

    fun logAchievementUnlocked(achievementType: String, points: Int) {
        val bundle = Bundle().apply {
            putString("achievement_type", achievementType)
            putInt("points", points)
        }
        analytics.logEvent("achievement_unlocked", bundle)
    }

    // User Engagement
    fun logDailyGoalAchieved() {
        analytics.logEvent("daily_goal_achieved", null)
    }

    fun logStreakUpdated(currentStreak: Int) {
        val bundle = Bundle().apply {
            putInt("current_streak", currentStreak)
        }
        analytics.logEvent("streak_updated", bundle)
    }

    fun logNotificationClicked(templateId: String, segment: String) {
        val bundle = Bundle().apply {
            putString("template_id", templateId)
            putString("segment", segment)
        }
        analytics.logEvent("notification_clicked", bundle)
    }
    
    // AI Features
    fun logAiFeatureUsed(featureType: String) {
        val bundle = Bundle().apply {
            putString("feature_type", featureType)
        }
        analytics.logEvent("ai_feature_used", bundle)
    }
}
