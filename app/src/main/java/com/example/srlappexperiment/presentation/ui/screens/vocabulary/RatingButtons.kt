package com.example.srlappexperiment.presentation.ui.screens.vocabulary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import com.example.srlappexperiment.ui.theme.*

/**
 * Rating quality values matching SM-2 algorithm
 */
object RatingQuality {
    const val AGAIN = 1
    const val HARD = 3
    const val GOOD = 4
    const val EASY = 5
}

@Composable
fun RatingButtons(
    onRating: (Int) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val ratings = listOf(
            Triple("Again", Error, RatingQuality.AGAIN),
            Triple("Hard", Warning, RatingQuality.HARD),
            Triple("Good", PrimaryBlue, RatingQuality.GOOD),
            Triple("Easy", Success, RatingQuality.EASY)
        )

        ratings.forEach { (text, color, quality) ->
            RatingButton(
                text = text,
                color = color,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onRating(quality)
                }
            )
        }
    }
}

@Composable
fun RatingButton(
    text: String,
    color: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = if (enabled) 0.15f else 0.05f),
        contentColor = if (enabled) color else color.copy(alpha = 0.4f),
        border = if (enabled) androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f)) else null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
