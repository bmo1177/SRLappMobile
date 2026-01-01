package com.example.srlappexperiment.presentation.ui.screens.curriculum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.foundation.BorderStroke
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.srlappexperiment.presentation.viewmodel.CurriculumViewModel
import com.example.srlappexperiment.data.local.database.entities.*

@Composable
fun CurriculumScreen(
    viewModel: CurriculumViewModel = hiltViewModel(),
    onNavigateToLesson: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedDomainId by remember { mutableStateOf<Int?>(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Spanish Learning Path", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Elementary A2", color = PrimaryPurple, fontWeight = FontWeight.Medium)
            }
            TextButton(onClick = { }) {
                Text("Switch", color = PrimaryPurple)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress Overview Card
            item {
                ModernCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SurfaceLight
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                            CircularProgressIndicator(
                                progress = { 0.15f },
                                modifier = Modifier.fillMaxSize(),
                                color = PrimaryPurple,
                                strokeWidth = 8.dp,
                                trackColor = PrimaryPurple.copy(alpha = 0.1f)
                            )
                            Text("15%", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Text("Enhanced Course Path", fontWeight = FontWeight.Bold)
                            Text("Based on teacher's syllabus", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onNavigateToLesson("next") },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                            ) {
                                Text("Resume")
                            }
                        }
                    }
                }
            }

            // Domains from Teacher's Database
            items(uiState.domains) { domain ->
                TeacherDomainItem(
                    domain = domain,
                    competences = uiState.competences[domain.domain_id] ?: emptyList(),
                    isExpanded = expandedDomainId == domain.domain_id,
                    onToggle = { expandedDomainId = if (expandedDomainId == domain.domain_id) null else domain.domain_id },
                    onLessonClick = onNavigateToLesson
                )
            }
        }
    }
}

@Composable
fun TeacherDomainItem(
    domain: TeacherDomain,
    competences: List<TeacherCompetence>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onLessonClick: (String) -> Unit
) {
    Column {
        ModernCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() },
            backgroundColor = SurfaceLight
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurple.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (domain.domain_id == 1) Icons.Default.Computer else Icons.Default.Functions,
                        contentDescription = null,
                        tint = PrimaryPurple
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        domain.name,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        "${competences.size} Competencies",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)) {
                competences.forEach { competence ->
                    TeacherCompetenceItem(competence, onLessonClick)
                }
            }
        }
    }
}

@Composable
fun TeacherCompetenceItem(competence: TeacherCompetence, onLessonClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onLessonClick(competence.competence_id.toString()) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = AccentCoral
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(competence.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Tap to view details", fontSize = 12.sp, color = TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
        }
    }
}
