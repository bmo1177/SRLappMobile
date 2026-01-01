package com.example.srlappexperiment.presentation.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.presentation.ui.components.*
import com.example.srlappexperiment.presentation.ui.components.onboarding.*
import com.example.srlappexperiment.ui.theme.*

/**
 * Tutorial screen showing how to use the app
 */
@Composable
fun TutorialScreen(
    onStart: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(0) }
    val steps = remember {
        listOf(
            TutorialStep(
                title = "Interactive Cards",
                description = "Tap to reveal meanings and hear pronunciations instantly.",
                icon = "🃏"
            ),
            TutorialStep(
                title = "Smart Spacing",
                description = "Our AI predicts when you're about to forget a word.",
                icon = "🧠"
            ),
            TutorialStep(
                title = "Daily Habits",
                description = "Complete your daily goal to build a streak and stay consistent.",
                icon = "🔥"
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
    ) {
        // Title
        OnboardingTitle(text = "Unlocking Mastery")

        Spacer(modifier = Modifier.height(8.dp))

        OnboardingDescription(text = "Quickly learn how to get the most out of SRL Mastery.")

        Spacer(modifier = Modifier.height(48.dp))

        // Tutorial showcase
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            TutorialShowcase(
                step = steps[currentStep]
            )
        }

        // Step indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (index == currentStep) 24.dp else 8.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (index == currentStep) PrimaryPurple else SilverAccent.copy(alpha = 0.5f))
                )
            }
        }

        // Navigation
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentStep < steps.size - 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OnboardingSecondaryButton(
                        text = "Skip",
                        onClick = onSkip
                    )

                    Button(
                        onClick = { currentStep++ },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(56.dp).width(140.dp)
                    ) {
                        Text("Next", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                OnboardingButton(
                    text = "Let's Go!",
                    onClick = onStart
                )
            }
        }
    }
}

data class TutorialStep(
    val title: String,
    val description: String,
    val icon: String
)

@Composable
fun TutorialShowcase(
    step: TutorialStep,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ModernCard(
            modifier = Modifier
                .size(width = 300.dp, height = 220.dp)
                .scale(scale),
            backgroundColor = SurfaceLight,
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, SilverAccent.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = step.icon, fontSize = 40.sp)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = step.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
            lineHeight = 28.sp
        )
    }
}

