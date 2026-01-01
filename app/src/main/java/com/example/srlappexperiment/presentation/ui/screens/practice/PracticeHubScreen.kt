package com.example.srlappexperiment.presentation.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.R
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard

@Composable
fun PracticeHubScreen(
    onStartReview: (Boolean) -> Unit, // True if quick review
    onViewLibrary: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Vocabulary Practice", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Due Review Card
        ModernCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFFF44336), Color(0xFFFF9800))))
                    .padding(24.dp)
            ) {
                Column {
                    Text("⏰ 23 words need review", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Review now to maintain your 87% retention rate!", color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onStartReview(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Start Review Session", color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Text("Review Sessions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        ReviewOptionItem(
            title = "Quick Review (5 min)",
            subtitle = "10 most urgent cards",
            icon = Icons.Default.FlashOn,
            color = PrimaryPurple,
            onClick = { onStartReview(true) }
        )

        ReviewOptionItem(
            title = "Full Review (15 min)",
            subtitle = "All due cards",
            icon = Icons.Default.LibraryBooks,
            color = PrimaryBlue,
            onClick = { onStartReview(false) }
        )
        
        ReviewOptionItem(
            title = "Custom Review",
            subtitle = "Select number of cards",
            icon = Icons.Default.Tune,
            color = AccentCoral,
            onClick = { }
        )

        Spacer(modifier = Modifier.height(8.dp))
        
        ModernCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewLibrary() },
            backgroundColor = SurfaceLight
        ) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryPurple)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Browse Vocabulary Library", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Composable
fun ReviewOptionItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    ModernCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        backgroundColor = SurfaceLight
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}
