package com.example.srlappexperiment.presentation.ui.screens.onboarding

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.presentation.ui.components.onboarding.*
import com.example.srlappexperiment.presentation.viewmodel.onboarding.UserType
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard

/**
 * User type selection screen
 */
@Composable
fun UserTypeScreen(
    selectedUserType: UserType?,
    onUserTypeSelected: (UserType) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
    ) {
        // Title
        OnboardingTitle(
            text = "Choose your learning goal"
        )

        Spacer(modifier = Modifier.height(8.dp))

        OnboardingDescription(
            text = "Select the path that matches your current priorities."
        )

        Spacer(modifier = Modifier.height(32.dp))

        // User type options
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserTypeCard(
                title = "IELTS Mastery",
                description = "Focus on academic vocabulary and exam strategies.",
                isSelected = selectedUserType == UserType.IELTS,
                onSelected = { onUserTypeSelected(UserType.IELTS) },
                icon = "🎓"
            )

            UserTypeCard(
                title = "TOEFL Success",
                description = "Essential vocabulary for iBT success and university life.",
                isSelected = selectedUserType == UserType.TOEFL,
                onSelected = { onUserTypeSelected(UserType.TOEFL) },
                icon = "📚"
            )

            UserTypeCard(
                title = "General Fluency",
                description = "Everyday communication and business English.",
                isSelected = selectedUserType == UserType.GENERAL,
                onSelected = { onUserTypeSelected(UserType.GENERAL) },
                icon = "🌍"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation buttons
        OnboardingNavigationButtons(
            showBack = true,
            nextButtonText = "Continue",
            onNext = onNext,
            onBack = onBack,
            canProceed = selectedUserType != null
        )
    }
}

/**
 * Reusable card for user type selection
 */
@Composable
fun UserTypeCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    icon: String,
    modifier: Modifier = Modifier
) {
    ModernCard(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelected
            ),
        backgroundColor = if (isSelected) SurfaceLight else SurfaceLight,
        shape = RoundedCornerShape(20.dp)
    ) {
        val borderColor = if (isSelected) PrimaryPurple else Color.Transparent
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isSelected) PrimaryPurple.copy(alpha = 0.05f) else Color.Transparent,
            border = if (isSelected) BorderStroke(2.dp, PrimaryPurple) else BorderStroke(1.dp, SilverAccent.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon/Visual Tile element with gradient
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) 
                                androidx.compose.ui.graphics.Brush.linearGradient(listOf(PrimaryPurple, PrimaryBlue))
                            else 
                                androidx.compose.ui.graphics.Brush.linearGradient(listOf(SilverAccent.copy(alpha = 0.1f), SilverAccent.copy(alpha = 0.2f)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PrimaryPurple else TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

