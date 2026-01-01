package com.example.srlappexperiment.presentation.ui.screens.roadmap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.domain.model.LearningRoadmap
import com.example.srlappexperiment.domain.model.RoadmapFactory
import com.example.srlappexperiment.domain.model.RoadmapWeek
import com.example.srlappexperiment.domain.model.SessionStatus
import com.example.srlappexperiment.ui.theme.*
import kotlin.math.sin

/**
 * Main screen for the learning roadmap
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningRoadmapScreen(
    onNavigateToSession: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    // In a real app, this would come from a ViewModel
    val roadmap = remember { 
        RoadmapFactory.createDefaultRoadmap(
            completedSessionIds = setOf(1, 2, 3) // Mock some progress
        )
    }
    
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (roadmap.currentSession / 2).coerceAtLeast(0)
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learning Path") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Stats
                item {
                    RoadmapHeader(roadmap)
                }
                
                // Weeks and Sessions
                items(roadmap.weeks) { week ->
                    WeekSection(
                        week = week,
                        onSessionClick = { session ->
                            onNavigateToSession(session.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RoadmapHeader(roadmap: LearningRoadmap) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Week ${roadmap.currentWeek}",
                color = GoldAccent,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { roadmap.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = GoldAccent,
                trackColor = Color.White.copy(alpha = 0.2f),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${roadmap.completedSessions} / ${roadmap.totalSessions} Sessions Completed",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun WeekSection(
    week: RoadmapWeek,
    onSessionClick: (com.example.srlappexperiment.domain.model.LearningSession) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Week Header
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .background(
                    if (week.isUnlocked) PrimaryLight.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "UNIT ${week.weekNumber}: ${week.theme}",
                color = if (week.isUnlocked) PrimaryPurple else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        
        // Sessions in winding path
        week.sessions.forEachIndexed { index, session ->
            // Calculate horizontal offset for winding path effect
            // Sine wave pattern
            val amplitude = 80.dp
            // Unique offset for each session across the whole roadmap would be better, 
            // but per-week is okay for now if we alternate start direction
            val globalIndex = (week.weekNumber - 1) * 2 + index
            val horizontalOffset = amplitude * sin(globalIndex.toFloat() * 1.5f)
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), // Height for connector space
                contentAlignment = Alignment.Center
            ) {
                // Draw path to next node (if strictly below)
                // Note: Drawing mostly accurate paths in LazyColumn is complex. 
                // For this MVP, we focus on the nodes placement.
                // A vertical dashed line could be a simple fallback connector.
                if (index < week.sessions.size - 1 || week.weekNumber < 20) {
                     Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(60.dp)
                            .offset(y = 60.dp, x = horizontalOffset) // Center below node
                            .background(
                                color = if (session.status == SessionStatus.COMPLETED) GoldAccent else Color.LightGray.copy(alpha = 0.5f)
                            )
                    )
                }

                RoadmapNode(
                    session = session,
                    onClick = { onSessionClick(session) },
                    modifier = Modifier.offset(x = horizontalOffset)
                )
            }
        }
    }
}
