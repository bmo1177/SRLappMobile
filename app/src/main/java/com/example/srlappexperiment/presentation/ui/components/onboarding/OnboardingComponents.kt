package com.example.srlappexperiment.presentation.ui.components.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.presentation.viewmodel.onboarding.OnboardingStep
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.GradientButton

/**
 * Progress indicator showing current step (e.g., "1/4")
 */
@Composable
fun OnboardingProgressIndicator(
    currentStep: OnboardingStep,
    modifier: Modifier = Modifier
) {
    val stepIndex = when (currentStep) {
        OnboardingStep.WELCOME -> 0
        OnboardingStep.LANGUAGE_SELECTION -> 1
        OnboardingStep.USER_TYPE -> 2
        OnboardingStep.GOAL_SETTING -> 3
        OnboardingStep.TUTORIAL -> 4
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == stepIndex) 24.dp else 8.dp, 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (index == stepIndex) PrimaryPurple else SilverAccent.copy(alpha = 0.5f))
            )
        }
    }
}

/**
 * Primary button for onboarding actions
 */
@Composable
fun OnboardingButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    GradientButton(
        text = text,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Secondary button for skip/back actions
 */
@Composable
fun OnboardingSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = PrimaryPurple,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Navigation buttons row with Back/Next or Skip/Next
 */
@Composable
fun OnboardingNavigationButtons(
    showBack: Boolean = false,
    showSkip: Boolean = false,
    nextButtonText: String = "Next",
    onNext: () -> Unit,
    onBack: () -> Unit = {},
    onSkip: () -> Unit = {},
    canProceed: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            OnboardingSecondaryButton(
                text = "Back",
                onClick = onBack
            )
        } else if (showSkip) {
            OnboardingSecondaryButton(
                text = "Skip",
                onClick = onSkip
            )
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }

        OnboardingButton(
            text = nextButtonText,
            onClick = onNext,
            enabled = canProceed,
            modifier = Modifier.width(120.dp)
        )
    }
}

/**
 * Section title for onboarding screens
 */
@Composable
fun OnboardingTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        modifier = modifier.fillMaxWidth().padding(bottom = 12.dp),
        color = TextPrimary,
        textAlign = TextAlign.Center
    )
}

/**
 * Section description for onboarding screens
 */
@Composable
fun OnboardingDescription(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        lineHeight = 26.sp
    )
}

