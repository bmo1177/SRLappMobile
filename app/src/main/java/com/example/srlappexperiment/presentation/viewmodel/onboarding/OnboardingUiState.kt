package com.example.srlappexperiment.presentation.viewmodel.onboarding

import com.example.srlappexperiment.domain.model.User

/**
 * Sealed class representing onboarding UI state
 */
sealed class OnboardingUiState {
    object Initial : OnboardingUiState()
    object Loading : OnboardingUiState()
    object Unauthenticated : OnboardingUiState()
    data class Success(val user: User) : OnboardingUiState()
    data class Error(val message: String) : OnboardingUiState()
}

/**
 * Onboarding screen step
 */
enum class OnboardingStep {
    WELCOME,
    LANGUAGE_SELECTION,
    USER_TYPE,
    GOAL_SETTING,
    TUTORIAL
}

/**
 * User type for learning goal
 */
enum class UserType {
    IELTS,
    TOEFL,
    GENERAL
}

/**
 * Data class for goal settings
 */
data class GoalSettings(
    val userType: UserType? = null,
    val targetScore: Int? = null,
    val examDate: Long? = null,
    val dailyStudyTimeMinutes: Int = 30
)

