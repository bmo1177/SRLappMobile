package com.example.srlappexperiment.presentation.ui.screens.vocabulary

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard

/**
 * Flashcard widget with flip animation for vocabulary practice
 */
@Composable
fun FlashcardWidget(
    card: VocabularyCard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onPronounce: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "cardFlip"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(onClick = onFlip),
        contentAlignment = Alignment.Center
    ) {
        ModernCard(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = if (rotation > 90f) SurfaceLight else SurfaceLight, // Consistent base
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, SilverAccent.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
                    .graphicsLayer {
                        rotationY = if (rotation > 90f) 180f else 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    FlashcardFront(card = card, onPronounce = onPronounce)
                } else {
                    FlashcardBack(card = card)
                }
            }
        }
    }
}

@Composable
private fun FlashcardFront(
    card: VocabularyCard,
    onPronounce: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(32.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Difficulty indicator at top left
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
            DifficultyBadge(difficulty = card.difficulty)
        }
        
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = card.word,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryPurple,
            textAlign = TextAlign.Center,
            letterSpacing = (-1).sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (card.pronunciation.isNotBlank()) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPronounce(card.word) }
                    .background(PrimaryPurple.copy(alpha = 0.05f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Simple waveform animation metaphor (visual only for now)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(3) { i ->
                        Box(Modifier.width(2.dp).height(12.dp + (i*4).dp).background(PrimaryPurple, CircleShape))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "/${card.pronunciation}/",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryPurple,
                    fontStyle = FontStyle.Italic
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1.2f))
        
        Surface(
            color = SilverAccent.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Tap to flip",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun FlashcardBack(card: VocabularyCard) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Column {
            Text(
                text = "Meaning",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = card.translation,
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryBlue,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.definition,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                lineHeight = 26.sp
            )
        }

        if (card.exampleSentence.isNotBlank()) {
            Column {
                Text(
                    text = "Example Usage",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = PrimaryPurple.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = "\"${card.exampleSentence}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(20.dp),
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val color = when (difficulty.lowercase()) {
        "beginner" -> Success
        "intermediate" -> Warning
        "advanced" -> Error
        else -> SilverAccent
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = difficulty.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
