package com.example.srlappexperiment.presentation.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Public
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Global, 1: Friends

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceLight),
                horizontalArrangement = Arrangement.Center
            ) {
                LeaderboardTab(
                    icon = Icons.Default.Public,
                    label = "Global",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                LeaderboardTab(
                    icon = Icons.Default.Groups,
                    label = "Friends",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            // Top 3 Podium Placeholder
            PodiumSection()

            // List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(mockRankings) { index, user ->
                    RankingItem(index + 4, user)
                }
            }
        }
    }
}

@Composable
fun LeaderboardTab(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        onClick = onClick,
        color = if (isSelected) PrimaryPurple else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isSelected) Color.White else TextSecondary)
            Spacer(Modifier.width(8.dp))
            Text(label, color = if (isSelected) Color.White else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun PodiumSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PodiumStand("User 2", "2.1k", Modifier.height(130.dp), SilverAccent)
        PodiumStand("User 1", "2.5k", Modifier.height(160.dp), GoldAccent)
        PodiumStand("User 3", "1.9k", Modifier.height(110.dp), BronzeAccent)
    }
}

@Composable
fun PodiumStand(name: String, xp: String, heightModifier: Modifier, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = color)
        }
        Spacer(Modifier.height(8.dp))
        Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text("$xp XP", fontSize = 10.sp, color = TextSecondary)
        Spacer(Modifier.height(12.dp))
        Box(modifier = heightModifier.width(60.dp).clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)).background(color))
    }
}

@Composable
fun RankingItem(rank: Int, user: RankingUser) {
    ModernCard(modifier = Modifier.fillMaxWidth(), backgroundColor = SurfaceLight) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("#$rank", fontWeight = FontWeight.ExtraBold, color = TextSecondary, modifier = Modifier.width(32.dp))
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray))
            Spacer(Modifier.width(16.dp))
            Text(user.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("${user.xp} XP", fontWeight = FontWeight.ExtraBold, color = PrimaryPurple)
        }
    }
}

data class RankingUser(val name: String, val xp: Int)
val mockRankings = listOf(
    RankingUser("Sofia", 1850),
    RankingUser("Mateo", 1720),
    RankingUser("Isabella", 1600),
    RankingUser("Santiago", 1540),
    RankingUser("Elena", 1400)
)
val GoldAccent = Color(0xFFFFD700)
val SilverAccent = Color(0xFFC0C0C0)
val BronzeAccent = Color(0xFFCD7F32)
