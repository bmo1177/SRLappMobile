package com.example.srlappexperiment.presentation.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentCMSScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lessons", "Vocabulary", "Exercises")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Content CMS", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Create logic */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Content")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BackgroundLight,
                contentColor = PrimaryPurple,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryPurple
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (selectedTab) {
                0 -> LessonListSection()
                1 -> VocabListSection()
                2 -> ExerciseListSection()
            }
        }
    }
}

@Composable
fun LessonListSection() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("English Foundations (Section 1)", fontWeight = FontWeight.Bold, color = TextSecondary)
        }
        items(mockLessonList) { lesson ->
            LessonCMSItem(lesson)
        }
    }
}

@Composable
fun LessonCMSItem(lesson: LessonSummary) {
    ModernCard(modifier = Modifier.fillMaxWidth().clickable { }, backgroundColor = SurfaceLight) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(lesson.title, fontWeight = FontWeight.Bold)
                Text("${lesson.duration}m | ${lesson.vocabCount} words", fontSize = 12.sp, color = TextSecondary)
            }
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (lesson.isPublished) Success.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
            ) {
                Text(
                    if (lesson.isPublished) "Published" else "Draft",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (lesson.isPublished) Success else TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp), tint = PrimaryBlue)
        }
    }
}

@Composable
fun VocabListSection() {
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(48.dp), tint = PrimaryPurple.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Bulk Vocabulary Importer", fontWeight = FontWeight.Bold)
        Text("Import CSV or JSON word lists", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)) {
            Text("Select File")
        }
    }
}

@Composable
fun ExerciseListSection() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Exercise Builder Coming Soon", color = TextSecondary)
    }
}

data class LessonSummary(val id: String, val title: String, val duration: Int, val vocabCount: Int, val isPublished: Boolean)

val mockLessonList = listOf(
    LessonSummary("1", "Lesson 1: First Words", 15, 15, true),
    LessonSummary("2", "Lesson 2: Introductions", 15, 15, true),
    LessonSummary("3", "Lesson 3: Numbers 1-100", 20, 15, false),
    LessonSummary("4", "Lesson 4: Common Objects", 15, 15, true)
)
