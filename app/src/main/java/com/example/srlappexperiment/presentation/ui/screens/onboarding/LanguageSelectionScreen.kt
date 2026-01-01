package com.example.srlappexperiment.presentation.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.presentation.ui.components.onboarding.*
import com.example.srlappexperiment.ui.theme.*

data class LanguageOption(
    val name: String,
    val flag: String,
    val code: String
)

@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = listOf(
        LanguageOption("English", "🇺🇸", "en"),
        LanguageOption("French", "🇫🇷", "fr"),
        LanguageOption("Spanish", "🇪🇸", "es"),
        LanguageOption("German", "🇩🇪", "de"),
        LanguageOption("Chinese", "🇨🇳", "zh"),
        LanguageOption("Japanese", "🇯🇵", "ja")
    )

    var selectedLanguage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
    ) {
        OnboardingTitle(text = "What language do you want to learn?")
        Spacer(modifier = Modifier.height(8.dp))
        OnboardingDescription(text = "Choose your target language to start your journey.")

        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(languages) { lang ->
                LanguageTile(
                    language = lang,
                    isSelected = selectedLanguage == lang.code,
                    onClick = {
                        selectedLanguage = lang.code
                        onLanguageSelected(lang.code)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OnboardingNavigationButtons(
            showBack = true,
            nextButtonText = "Continue",
            onNext = onNext,
            onBack = onBack,
            canProceed = selectedLanguage != null
        )
    }
}

@Composable
fun LanguageTile(
    language: LanguageOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PrimaryPurple else SilverAccent.copy(alpha = 0.3f)
    val backgroundColor = if (isSelected) PrimaryPurple.copy(alpha = 0.05f) else SurfaceLight

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = language.flag, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = language.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PrimaryPurple else TextPrimary
            )
        }
    }
}
