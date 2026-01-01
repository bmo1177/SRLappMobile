package com.example.srlappexperiment.presentation.ui.screens.lesson.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard

@Composable
fun ListeningExercise(
    phrase: String,
    translation: String,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Listen and repeat", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.1f))
                .clickable { /* Play audio */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(48.dp), tint = PrimaryBlue)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ModernCard(modifier = Modifier.fillMaxWidth(), backgroundColor = SurfaceLight) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(phrase, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(translation, fontSize = 16.sp, color = TextSecondary)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("I've finished", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DialogueExercise(
    scenario: String,
    dialogues: List<Pair<String, String>>, // Speaker to Text
    onComplete: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Dialogue Practice: $scenario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            dialogues.forEach { (speaker, text) ->
                val isUser = speaker == "You"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    ModernCard(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        backgroundColor = if (isUser) PrimaryPurple.copy(alpha = 0.1f) else SurfaceLight
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(speaker, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isUser) PrimaryPurple else PrimaryBlue)
                            Text(text, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
        ) {
            Text("Complete Conversation", fontWeight = FontWeight.Bold)
        }
    }
}
