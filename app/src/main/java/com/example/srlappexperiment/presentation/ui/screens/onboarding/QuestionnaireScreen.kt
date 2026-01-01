package com.example.srlappexperiment.presentation.ui.screens.onboarding

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.PrimaryPurple
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.srlappexperiment.presentation.viewmodel.onboarding.OnboardingViewModel
import com.example.srlappexperiment.presentation.viewmodel.onboarding.OnboardingStep
import com.example.srlappexperiment.presentation.viewmodel.onboarding.OnboardingUiState
import com.example.srlappexperiment.presentation.viewmodel.onboarding.UserType

@Composable
fun QuestionnaireScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val goalSettings by viewModel.goalSettings.collectAsState()
    
    // Navigate on success
    LaunchedEffect(uiState) {
        if (uiState is OnboardingUiState.Success) {
            onComplete()
        }
    }

    val progress = when(currentStep) {
        OnboardingStep.WELCOME -> 0.2f
        OnboardingStep.LANGUAGE_SELECTION -> 0.4f
        OnboardingStep.USER_TYPE -> 0.6f
        OnboardingStep.GOAL_SETTING -> 0.8f
        OnboardingStep.TUTORIAL -> 1.0f
    }

    Scaffold(
        topBar = {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = PrimaryPurple,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep != OnboardingStep.WELCOME) {
                    TextButton(onClick = { viewModel.previousStep() }) {
                        Text("Back", color = Color.Gray)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        if (currentStep == OnboardingStep.TUTORIAL) {
                            viewModel.completeOnboarding()
                        } else {
                            viewModel.nextStep()
                        }
                    },
                    enabled = viewModel.canProceedToNext() && uiState !is OnboardingUiState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    if (uiState is OnboardingUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text(if (currentStep == OnboardingStep.TUTORIAL) "Start My Journey" else "Continue")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (currentStep) {
                OnboardingStep.WELCOME -> WelcomeStep()
                OnboardingStep.LANGUAGE_SELECTION -> LanguageSelectionStep(null) { /* TBD in ViewModel if needed */ }
                OnboardingStep.USER_TYPE -> LevelSelectionStep(goalSettings.userType?.name) { viewModel.setUserType(UserType.valueOf(it)) }
                OnboardingStep.GOAL_SETTING -> GoalSelectionStep()
                OnboardingStep.TUTORIAL -> NotificationStep()
            }
            
            if (uiState is OnboardingUiState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (uiState as OnboardingUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable fun WelcomeStep() {
    Text("Welcome to SRL App!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Let's personalize your learning experience in just a few steps.", textAlign = TextAlign.Center, color = Color.Gray)
}

@Composable
fun LanguageSelectionStep(selected: String?, onSelect: (String) -> Unit) {
    Text("Which language do you want to learn?", fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(24.dp))
    LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val languages = listOf("Spanish" to "🇪🇸", "French" to "🇫🇷", "German" to "🇩🇪", "Italian" to "🇮🇹", "Japanese" to "🇯🇵", "Korean" to "🇰🇷")
        items(languages) { (name, flag) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(name) }
                    .border(if (selected == name) 2.dp else 0.dp, PrimaryPurple, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (selected == name) PrimaryPurple.copy(alpha = 0.1f) else Color.White)
            ) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(flag, fontSize = 40.sp)
                    Text(name, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun LevelSelectionStep(selected: String?, onSelect: (String) -> Unit) {
    Text("How much do you know?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(24.dp))
    val levels = listOf("Beginner" to "🌱 Starting fresh", "Elementary" to "🌿 Basic words", "Intermediate" to "🌳 Simple chats", "Advanced" to "🌲 Comfortable")
    levels.forEach { (name, desc) ->
        val userTypeName = when(name) {
            "Beginner" -> "GENERAL"
            "Elementary" -> "GENERAL" // Simplified
            "Intermediate" -> "IELTS" 
            "Advanced" -> "TOEFL"
            else -> "GENERAL"
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { onSelect(userTypeName) }
                .border(if (selected == userTypeName) 2.dp else 0.dp, PrimaryPurple, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Text(desc, color = Color.Gray)
            }
        }
    }
}

@Composable fun GoalSelectionStep() { Text("What's your goal?", fontSize = 22.sp, fontWeight = FontWeight.Bold) /* Simplified for demo */ }
@Composable fun NotificationStep() { Text("Enable Notifications", fontSize = 22.sp, fontWeight = FontWeight.Bold) /* Simplified for demo */ }
