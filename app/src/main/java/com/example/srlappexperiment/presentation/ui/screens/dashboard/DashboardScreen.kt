package com.example.srlappexperiment.presentation.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.srlappexperiment.R
import com.example.srlappexperiment.domain.model.SyncStatus
import com.example.srlappexperiment.presentation.ui.components.*
import com.example.srlappexperiment.presentation.ui.components.dashboard.*
import com.example.srlappexperiment.presentation.ui.components.gamification.*
import com.example.srlappexperiment.presentation.viewmodel.DashboardViewModel
import com.example.srlappexperiment.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToPractice: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToSummary: () -> Unit = {},
    onNavigateToAiPartner: () -> Unit = {},
    onNavigateToPlacement: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {}
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showFTUE by remember { mutableStateOf(false) }
    var ftueStep by remember { mutableIntStateOf(1) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "My Progress", 
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold 
                        ) 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight),
                    actions = {
                        val syncStatus by viewModel.syncStatus.collectAsState()
                        SyncIndicator(status = syncStatus, onSyncClick = { viewModel.triggerSync() })
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
                        }
                    }
                )
            },
            containerColor = BackgroundLight
        ) { padding ->
            if (isLoading) {
                DashboardSkeleton(padding)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Top Section: Greeting
                    item {
                        Column {
                            Text(
                                text = "Dashboard",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Track your progress",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    // Placement Nudge
                    item {
                        ModernCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToPlacement() },
                            backgroundColor = PrimaryBlue.copy(alpha = 0.05f)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, contentDescription = null, tint = PrimaryBlue)
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Personalize Your Path", fontWeight = FontWeight.Bold)
                                    Text("Take the level assessment", fontSize = 12.sp, color = TextSecondary)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
                            }
                        }
                    }

                    // Streak Card (Hero Section)
                    item {
                        PremiumCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = "🔥 ${stats.currentStreak} Day Streak",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Don't break the chain!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = onNavigateToPractice,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text("Practice Now", color = PrimaryPurple, fontWeight = FontWeight.Bold)
                                    }
                                }
                                
                                Box(contentAlignment = Alignment.Center) {
                                    ProgressRing(
                                        progress = (stats.currentProgress?.toFloat() ?: 0f) / 100f,
                                        modifier = Modifier.size(90.dp),
                                        strokeWidth = 20f,
                                        primaryColor = Color.White,
                                        secondaryColor = Color.White.copy(alpha = 0.2f)
                                    )
                                    Text(
                                        text = "${stats.currentProgress?.toInt() ?: 0}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Daily Lesson Section
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Today's Lesson", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            ModernCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = SurfaceLight
                            ) {
                                Column(Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .background(AccentCoral.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("Food & Dining", color = AccentCoral, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Beginner • ~12 min", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Ordering at a Restaurant", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(
                                        "Learn 15 new words and practice a real-world dialogue scene.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    LinearProgressIndicator(
                                        progress = { 0.6f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(CircleShape),
                                        color = Success,
                                        trackColor = Success.copy(alpha = 0.1f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("60% complete", fontSize = 12.sp, color = Success)
                                        Text("You learned 9 words so far!", fontSize = 12.sp, color = TextSecondary)
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = onNavigateToPractice,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                                    ) {
                                        Text("Continue Lesson")
                                    }
                                }
                            }
                        }
                    }

                    // Quick Actions Grid (Refactored to items in LazyColumn)
                    item {
                        Text("Quick Practice", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(1f)) { QuickActionCard("Vocabulary", "${stats.dueCardsCount} words", R.drawable.ic_learning_flashcard, PrimaryPurple) { onNavigateToLibrary() } }
                            Box(Modifier.weight(1f)) { QuickActionCard("Speaking", "AI Partner", R.drawable.ic_learning_mic, PrimaryBlue) { onNavigateToAiPartner() } }
                        }
                    }
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(1f)) { QuickActionCard("Listening", "Sharpen ear", R.drawable.ic_nav_search, AccentCoral) { } }
                            Box(Modifier.weight(1f)) { QuickActionCard("Quiz", "5-min", R.drawable.ic_progress_trophy, Success) { } }
                        }
                    }

                    // Progress Snapshot
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Your Progress This Week", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            ModernCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = SurfaceLight
                            ) {
                                Column(Modifier.padding(20.dp)) {
                                    LineChart(
                                        data = stats.dailyStats,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        StatMiniItem("Words", "${stats.totalWordsLearned}", Success)
                                        StatMiniItem("Accuracy", "${stats.averageAccuracy.toInt()}%", PrimaryBlue)
                                        StatMiniItem("Hours", String.format("%.1f", stats.totalStudyHours), AccentCoral)
                                    }
                                }
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }

        // FTUE Overlay
        if (showFTUE) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.7f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (ftueStep) {
                        1 -> {
                            Text("Get Started", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Let's take a quick tour of your dashboard.", color = Color.White.copy(alpha = 0.8f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                        2 -> {
                            Text("Your Daily Lesson 🔥", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Every day we prepare a curated session just for you.", color = Color.White.copy(alpha = 0.8f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                        3 -> {
                            Text("Track Your Gains 📈", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("See your memory strength and growth in the Progress tab.", color = Color.White.copy(alpha = 0.8f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { 
                            if (ftueStep < 3) ftueStep++ else showFTUE = false 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        Text(if (ftueStep < 3) "Next" else "Got it!")
                    }
                }
            }
        }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
            Text(subtitle, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable
fun StatMiniItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = color)
        Text(label, fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
fun SyncIndicator(status: SyncStatus, onSyncClick: () -> Unit) {
    val tint = when (status) {
        is SyncStatus.Syncing -> PrimaryPurple
        is SyncStatus.Error -> com.example.srlappexperiment.ui.theme.Error
        is SyncStatus.Success -> com.example.srlappexperiment.ui.theme.Success
        else -> TextSecondary
    }

    val icon = when (status) {
        is SyncStatus.Error -> Icons.Default.SyncProblem
        is SyncStatus.Success -> Icons.Default.CheckCircle
        else -> Icons.Default.Sync
    }

    IconButton(onClick = onSyncClick, enabled = status !is SyncStatus.Syncing) {
        if (status is SyncStatus.Syncing) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = tint,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = "Sync",
                tint = tint
            )
        }
    }
}

@Composable
fun DashboardSkeleton(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SkeletonItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(28.dp)
        )
        SkeletonItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(24.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SkeletonItem(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                shape = RoundedCornerShape(24.dp)
            )
            SkeletonItem(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}
