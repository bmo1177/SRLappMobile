package com.example.srlappexperiment.presentation.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.srlappexperiment.presentation.ui.components.onboarding.OnboardingProgressIndicator
import com.example.srlappexperiment.presentation.viewmodel.onboarding.OnboardingStep
import com.example.srlappexperiment.presentation.viewmodel.onboarding.OnboardingViewModel

/**
 * Main onboarding screen container
 * Handles navigation between all onboarding steps
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val goalSettings by viewModel.goalSettings.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Handle completion
    LaunchedEffect(uiState) {
        when (uiState) {
            is com.example.srlappexperiment.presentation.viewmodel.onboarding.OnboardingUiState.Success -> {
                onComplete()
            }
            else -> {}
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Progress indicator
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
            ) {
                OnboardingProgressIndicator(currentStep = currentStep)
            }
        }

        // Content
        when (currentStep) {
            OnboardingStep.WELCOME -> {
                WelcomeScreen(
                    onGetStarted = { viewModel.nextStep() },
                    onSkip = onSkip,
                    modifier = Modifier.fillMaxSize()
                )
            }
            OnboardingStep.LANGUAGE_SELECTION -> {
                LanguageSelectionScreen(
                    onLanguageSelected = { /* Mock for now */ },
                    onNext = { viewModel.nextStep() },
                    onBack = { viewModel.previousStep() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            OnboardingStep.USER_TYPE -> {
                UserTypeScreen(
                    selectedUserType = goalSettings.userType,
                    onUserTypeSelected = { viewModel.setUserType(it) },
                    onNext = {
                        if (viewModel.canProceedToNext()) {
                            viewModel.nextStep()
                        }
                    },
                    onBack = { viewModel.previousStep() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            OnboardingStep.GOAL_SETTING -> {
                GoalSettingScreen(
                    userType = goalSettings.userType,
                    targetScore = goalSettings.targetScore,
                    examDate = goalSettings.examDate,
                    dailyStudyTime = goalSettings.dailyStudyTimeMinutes,
                    wordsPerMonth = viewModel.calculateWordsPerMonth(),
                    onTargetScoreChange = { score -> viewModel.setTargetScore(score) },
                    onExamDateChange = { date -> viewModel.setExamDate(date) },
                    onStudyTimeChange = { time -> viewModel.setDailyStudyTime(time) },
                    onNext = {
                        if (viewModel.canProceedToNext()) {
                            viewModel.nextStep()
                        }
                    },
                    onBack = { viewModel.previousStep() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            OnboardingStep.TUTORIAL -> {
                TutorialScreen(
                    onStart = { viewModel.completeOnboarding() },
                    onSkip = { viewModel.skipOnboarding() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

