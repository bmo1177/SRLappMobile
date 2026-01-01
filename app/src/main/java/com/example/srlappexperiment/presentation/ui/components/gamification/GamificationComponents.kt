package com.example.srlappexperiment.presentation.ui.components.gamification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard

@Composable
fun DailyChallengesWidget(
    challenges: List<Challenge>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Daily Challenges",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        challenges.forEach { challenge ->
            ChallengeItem(challenge = challenge)
        }
    }
}

@Composable
private fun ChallengeItem(challenge: Challenge) {
    ModernCard(
        backgroundColor = SurfaceLight,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(challenge.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = challenge.icon,
                    contentDescription = null,
                    tint = challenge.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                LinearProgressIndicator(
                    progress = { challenge.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .padding(top = 8.dp)
                        .clip(CircleShape),
                    color = challenge.color,
                    trackColor = challenge.color.copy(alpha = 0.1f)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "${(challenge.progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = challenge.color
            )
        }
    }
}

data class Challenge(
    val title: String,
    val progress: Float,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun MasteryBadge(
    level: String,
    modifier: Modifier = Modifier
) {
    val (icon, color, label) = when (level.lowercase()) {
        "gold" -> Triple(Icons.Default.WorkspacePremium, GoldAccent, "Expert")
        "silver" -> Triple(Icons.Default.MilitaryTech, SilverAccent, "Adept")
        else -> Triple(Icons.Default.School, PrimaryBlue, "Novice")
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = color, 
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label, 
                style = MaterialTheme.typography.labelLarge, 
                fontWeight = FontWeight.ExtraBold, 
                color = color,
                letterSpacing = 0.5.sp
            )
        }
    }
}
