package com.example.srlappexperiment.presentation.viewmodel.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.srlappexperiment.data.analytics.AnalyticsManager
import com.example.srlappexperiment.domain.model.User
import com.example.srlappexperiment.domain.repository.AuthRepository
import com.example.srlappexperiment.domain.repository.PreferencesRepository
import com.example.srlappexperiment.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Initial)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _currentStep = MutableStateFlow<OnboardingStep>(OnboardingStep.WELCOME)
    val currentStep: StateFlow<OnboardingStep> = _currentStep.asStateFlow()

    private val _goalSettings = MutableStateFlow<GoalSettings>(GoalSettings())
    val goalSettings: StateFlow<GoalSettings> = _goalSettings.asStateFlow()

    init {
        loadOnboardingProgress()
        analyticsManager.logOnboardingStarted()
    }

    /**
     * Loads saved onboarding progress from DataStore
     */
    private fun loadOnboardingProgress() {
        viewModelScope.launch {
            val prefs = preferencesRepository.userPreferences.first()
            
            _goalSettings.value = GoalSettings(
                userType = try { UserType.valueOf(prefs.userType.uppercase()) } catch (e: Exception) { null },
                targetScore = prefs.targetScore,
                examDate = prefs.examDate,
                dailyStudyTimeMinutes = prefs.dailyGoalMinutes
            )

            // Restore step if exists
            try {
                _currentStep.value = OnboardingStep.valueOf(prefs.onboardingStep)
            } catch (e: Exception) {
                _currentStep.value = OnboardingStep.WELCOME
            }
        }
    }

    /**
     * Navigates to next onboarding step
     */
    fun nextStep() {
        val nextStep = when (_currentStep.value) {
            OnboardingStep.WELCOME -> OnboardingStep.LANGUAGE_SELECTION
            OnboardingStep.LANGUAGE_SELECTION -> OnboardingStep.USER_TYPE
            OnboardingStep.USER_TYPE -> OnboardingStep.GOAL_SETTING
            OnboardingStep.GOAL_SETTING -> OnboardingStep.TUTORIAL
            OnboardingStep.TUTORIAL -> null // Complete
        }
        nextStep?.let {
            _currentStep.value = it
            saveProgress()
        }
    }

    /**
     * Navigates to previous step
     */
    fun previousStep() {
        val prevStep = when (_currentStep.value) {
            OnboardingStep.WELCOME -> null
            OnboardingStep.LANGUAGE_SELECTION -> OnboardingStep.WELCOME
            OnboardingStep.USER_TYPE -> OnboardingStep.LANGUAGE_SELECTION
            OnboardingStep.GOAL_SETTING -> OnboardingStep.USER_TYPE
            OnboardingStep.TUTORIAL -> OnboardingStep.GOAL_SETTING
        }
        prevStep?.let {
            _currentStep.value = it
        }
    }

    /**
     * Sets user type selection
     */
    fun setUserType(userType: UserType) {
        _goalSettings.value = _goalSettings.value.copy(userType = userType)
        saveProgress()
    }

    /**
     * Sets target score
     */
    fun setTargetScore(score: Int) {
        if (score in Constants.MIN_TARGET_SCORE..Constants.MAX_TARGET_SCORE) {
            _goalSettings.value = _goalSettings.value.copy(targetScore = score)
            saveProgress()
        }
    }

    /**
     * Sets exam date
     */
    fun setExamDate(dateMillis: Long) {
        // Validate that exam date is in the future
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val selectedDate = Calendar.getInstance()
        selectedDate.timeInMillis = dateMillis
        selectedDate.set(Calendar.HOUR_OF_DAY, 0)
        selectedDate.set(Calendar.MINUTE, 0)
        selectedDate.set(Calendar.SECOND, 0)
        selectedDate.set(Calendar.MILLISECOND, 0)

        if (selectedDate.after(calendar)) {
            _goalSettings.value = _goalSettings.value.copy(examDate = dateMillis)
            saveProgress()
        }
    }

    /**
     * Sets daily study time
     */
    fun setDailyStudyTime(minutes: Int) {
        if (minutes in Constants.MIN_STUDY_TIME_MINUTES..Constants.MAX_STUDY_TIME_MINUTES) {
            _goalSettings.value = _goalSettings.value.copy(dailyStudyTimeMinutes = minutes)
            saveProgress()
        }
    }

    /**
     * Validates current step before proceeding
     */
    fun canProceedToNext(): Boolean {
        return when (_currentStep.value) {
            OnboardingStep.WELCOME -> true
            OnboardingStep.LANGUAGE_SELECTION -> true
            OnboardingStep.USER_TYPE -> _goalSettings.value.userType != null
            OnboardingStep.GOAL_SETTING -> {
                val settings = _goalSettings.value
                settings.userType != null &&
                (settings.userType == UserType.GENERAL || 
                 (settings.targetScore != null && settings.examDate != null))
            }
            OnboardingStep.TUTORIAL -> true
        }
    }

    /**
     * Calculates estimated words per month based on study time
     */
    fun calculateWordsPerMonth(): Int {
        val dailyMinutes = _goalSettings.value.dailyStudyTimeMinutes
        // Assume 2 minutes per card on average (review + new)
        val cardsPerDay = dailyMinutes / 2
        // 30 days per month
        return cardsPerDay * 30
    }

    /**
     * Completes onboarding and saves to Firestore
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Loading
            try {
                val currentUser = authRepository.getCurrentUser().first()
                if (currentUser != null) {
                    val settings = _goalSettings.value
                    
                    authRepository.updateUserProfile(
                        displayName = currentUser.displayName ?: ""
                    ).first()

                    // Mark onboarding as complete in PreferencesRepository
                    val prefs = com.example.srlappexperiment.domain.model.UserPreferences(
                        userType = settings.userType?.name?.lowercase() ?: "general",
                        targetScore = settings.targetScore,
                        examDate = settings.examDate,
                        dailyGoalMinutes = settings.dailyStudyTimeMinutes,
                        isOnboarded = true
                    )
                    preferencesRepository.updatePreferences(prefs)
                    
                    analyticsManager.logOnboardingCompleted()

                    _uiState.value = OnboardingUiState.Success(currentUser)
                } else {
                    _uiState.value = OnboardingUiState.Error("User not authenticated")
                }
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Error(e.message ?: "Failed to complete onboarding")
            }
        }
    }

    /**
     * Saves progress to DataStore
     */
    private fun saveProgress() {
        viewModelScope.launch {
            val settings = _goalSettings.value
            val currentPrefs = preferencesRepository.userPreferences.first()
            val updatedPrefs = currentPrefs.copy(
                onboardingStep = _currentStep.value.name,
                userType = settings.userType?.name?.lowercase() ?: "general",
                targetScore = settings.targetScore,
                examDate = settings.examDate,
                dailyGoalMinutes = settings.dailyStudyTimeMinutes
            )
            preferencesRepository.updatePreferences(updatedPrefs)
        }
    }

    /**
     * Marks onboarding as complete in DataStore
     */
    private fun markOnboardingComplete() {
        viewModelScope.launch {
            val currentPrefs = preferencesRepository.userPreferences.first()
            preferencesRepository.updatePreferences(currentPrefs.copy(isOnboarded = true))
        }
    }

    /**
     * Skips onboarding
     */
    fun skipOnboarding() {
        markOnboardingComplete()
        analyticsManager.logOnboardingSkipped()
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser().first()
            currentUser?.let {
                _uiState.value = OnboardingUiState.Success(it)
            } ?: run {
                _uiState.value = OnboardingUiState.Unauthenticated
            }
        }
    }
}

