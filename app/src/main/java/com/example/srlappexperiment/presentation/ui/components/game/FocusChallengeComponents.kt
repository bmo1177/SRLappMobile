package com.example.srlappexperiment.presentation.ui.components.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.data.local.database.entities.GameResult
import com.example.srlappexperiment.presentation.viewmodel.game.Question
import com.example.srlappexperiment.ui.theme.TealPrimary
import java.util.*

@Composable
fun TimerDisplay(seconds: Int) {
    val color = when {
        seconds < 5 -> Color.Red
        seconds < 20 -> Color(0xFFFFA000) // Amber
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = String.format("%d:%02d", seconds / 60, seconds % 60),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily
        )
    }
}

@Composable
fun QuestionCard(
    question: Question,
    onOptionSelected: (Int) -> Unit
) {
    var selectedIndex by remember(question) { mutableStateOf<Int?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = question.word,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TealPrimary
            )
            
            if (!question.pronunciation.isNullOrBlank()) {
                IconButton(onClick = { /* TTS */ }) {
                    Icon(Icons.Default.VolumeUp, null, tint = TealPrimary)
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                question.options.forEachIndexed { index, option ->
                    val isCorrect = index == question.correctIndex
                    val isSelected = index == selectedIndex
                    
                    val containerColor = when {
                        isSelected && isCorrect -> Color(0xFF4CAF50)
                        isSelected && !isCorrect -> Color(0xFFF44336)
                        selectedIndex != null && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.5f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Button(
                        onClick = { 
                            if (selectedIndex == null) {
                                selectedIndex = index
                                onOptionSelected(index)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = containerColor,
                            contentColor = if (selectedIndex != null) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        enabled = selectedIndex == null
                    ) {
                        Text(option, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreBoard(score: Int, accuracy: Float, combo: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Score", style = MaterialTheme.typography.labelMedium)
            Text(score.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        if (combo >= 2) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (combo >= 5) Color(0xFFFFD700) else TealPrimary)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "${combo}x COMBO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text("Accuracy", style = MaterialTheme.typography.labelMedium)
            Text("${accuracy.toInt()}%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LocalLeaderboard(results: List<GameResult>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFD700))
                Spacer(Modifier.width(8.dp))
                Text("Personal Best", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            results.take(5).forEachIndexed { index, result ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("#${index + 1}", fontWeight = FontWeight.Bold)
                    Text(result.score.toString(), fontWeight = FontWeight.ExtraBold)
                    Text(result.difficulty.uppercase(), fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    Text(
                        java.text.SimpleDateFormat("MMM dd", Locale.getDefault()).format(result.timestamp),
                        fontSize = 12.sp
                    )
                }
                if (index < results.size - 1) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
