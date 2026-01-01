package com.example.srlappexperiment.presentation.ui.screens.onboarding

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.presentation.viewmodel.onboarding.UserType
import com.example.srlappexperiment.presentation.ui.components.onboarding.*
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard
import java.text.SimpleDateFormat
import java.util.*

/**
 * Goal setting screen for personalization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSettingScreen(
    userType: UserType?,
    targetScore: Int?,
    examDate: Long?,
    dailyStudyTime: Int,
    wordsPerMonth: Int,
    onTargetScoreChange: (Int) -> Unit,
    onExamDateChange: (Long) -> Unit,
    onStudyTimeChange: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title
        OnboardingTitle(text = "Personalize Your Path")

        Spacer(modifier = Modifier.height(8.dp))

        OnboardingDescription(text = "Our AI will tailor the lessons to your schedule and goals.")

        Spacer(modifier = Modifier.height(32.dp))

        // Form fields
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Target Score (only for IELTS/TOEFL)
            if (userType == UserType.IELTS || userType == UserType.TOEFL) {
                TargetScoreSelector(
                    targetScore = targetScore ?: 6,
                    onScoreChange = onTargetScoreChange
                )
            }

            // Exam Date (only for IELTS/TOEFL)
            if (userType == UserType.IELTS || userType == UserType.TOEFL) {
                ExamDatePicker(
                    examDate = examDate,
                    onDateChange = onExamDateChange,
                    dateFormat = dateFormat
                )
            }

            // Daily Study Time
            DailyStudyTimeSlider(
                studyTimeMinutes = dailyStudyTime,
                onTimeChange = onStudyTimeChange
            )
            
            // AI Personality / Learning Style (New Feature)
            AIPersonalitySelector()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Estimated progress
        ModernCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PrimaryPurple.copy(alpha = 0.05f)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📈", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Estimated Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "You'll master ~$wordsPerMonth words per month",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // Navigation buttons
        val canProceed = if (userType == UserType.GENERAL) true else (targetScore != null && examDate != null)

        OnboardingNavigationButtons(
            showBack = true,
            nextButtonText = "Finish Setup",
            onNext = onNext,
            onBack = onBack,
            canProceed = canProceed
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIPersonalitySelector() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "AI Learning Style",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Friendly", "Rigorous", "Casual").forEach { style ->
                FilterChip(
                    selected = style == "Friendly", // Mock selection
                    onClick = { },
                    label = { Text(style) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TargetScoreSelector(
    targetScore: Int,
    onScoreChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Target Band Score",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (5..9).forEach { score ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onScoreChange(score) },
                    color = if (targetScore == score) PrimaryPurple else SurfaceLight,
                    border = BorderStroke(1.dp, if (targetScore == score) PrimaryPurple else SilverAccent.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = score.toString(),
                            fontWeight = FontWeight.Bold,
                            color = if (targetScore == score) Color.White else TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDatePicker(
    examDate: Long?,
    onDateChange: (Long) -> Unit,
    dateFormat: SimpleDateFormat
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Exam Date",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { showDatePicker = true },
            color = SurfaceLight,
            border = BorderStroke(1.dp, SilverAccent.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📅", modifier = Modifier.padding(end = 12.dp))
                Text(
                    text = if (examDate != null) dateFormat.format(Date(examDate)) else "Select your exam date",
                    color = if (examDate != null) TextPrimary else TextSecondary
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = examDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateChange(it) }
                    showDatePicker = false
                }) {
                    Text("OK", color = PrimaryPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DailyStudyTimeSlider(
    studyTimeMinutes: Int,
    onTimeChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daily Commitment",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "$studyTimeMinutes min",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple
            )
        }

        Slider(
            value = studyTimeMinutes.toFloat(),
            onValueChange = { onTimeChange(it.toInt()) },
            valueRange = 10f..60f,
            steps = 9,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = PrimaryPurple,
                activeTrackColor = PrimaryPurple,
                inactiveTrackColor = SilverAccent.copy(alpha = 0.3f)
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "10m", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(text = "60m", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

