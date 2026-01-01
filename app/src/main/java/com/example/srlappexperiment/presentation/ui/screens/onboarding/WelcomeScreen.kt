package com.example.srlappexperiment.presentation.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.srlappexperiment.presentation.ui.components.onboarding.*
import com.example.srlappexperiment.ui.theme.*

/**
 * Welcome screen - First screen of onboarding
 */
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.2f))

        // Logo representation with an organic shape and gradient
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(48.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryPurple, PrimaryBlue)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SRL",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Hero Headline
        OnboardingTitle(
            text = "Your Path to Language Mastery"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Engaging Description
        OnboardingDescription(
            text = "Unlock your full potential with AI-personalized learning paths and efficient spaced repetition."
        )

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Actions
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OnboardingButton(
                text = "Get Started",
                onClick = onGetStarted
            )

            OnboardingSecondaryButton(
                text = "I already have an account",
                onClick = onSkip
            )
        }
    }
}

