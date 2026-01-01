package com.example.srlappexperiment.presentation.ui.screens.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress & Insights", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Summary & Mastery Overview
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ModernCard(backgroundColor = SurfaceLight) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(PrimaryPurple.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Total Words", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("1,280", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            Text("+12% this week", style = MaterialTheme.typography.labelSmall, color = Success, fontWeight = FontWeight.Bold)
                        }
                    }
                    ModernCard(backgroundColor = SurfaceLight) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Success.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Success, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Retention", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("88%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            Text("High performance", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                ModernCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = SurfaceLight
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Mastery", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        MasteryDonutChart(percentage = 0.64f)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("64% Complete", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Success)
                    }
                }
            }

            // Cognitive UI: Memory Strength Overview
            MemoryStrengthSection()

            // Learning Time Chart
            ChartSection(title = "Learning Time (Min)") {
                WeeklyBarChart()
            }

            // Neurological Insight: Forgetting Curve
            ChartSection(
                title = "The Forgetting Curve",
                subtitle = "How Spaced Repetition keeps your knowledge peak"
            ) {
                ForgettingCurveChart()
            }

            // Vocabulary Growth Chart
            ChartSection(title = "Vocabulary Growth") {
                GrowthLineChart()
            }

            // Activity Heatmap
            ChartSection(title = "Study Activity") {
                ActivityHeatmap()
            }

            // Achievements Gallery
            AchievementSection()
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AchievementSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BadgeItem("7-Day Streak", "🔥", true)
            BadgeItem("Early Bird", "🌅", true)
            BadgeItem("100 Words", "📚", true)
            BadgeItem("Perfect Quiz", "🎯", false)
            BadgeItem("Conversationalist", "💬", false)
        }
    }
}

@Composable
private fun BadgeItem(title: String, emoji: String, isEarned: Boolean) {
    ModernCard(
        modifier = Modifier.width(110.dp),
        backgroundColor = if (isEarned) SurfaceLight else Color.Gray.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(if (isEarned) PrimaryPurple.copy(alpha = 0.1f) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 24.sp, modifier = Modifier.graphicsLayer(alpha = if (isEarned) 1f else 0.3f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = if (isEarned) TextPrimary else TextSecondary)
        }
    }
}

@Composable
private fun MemoryStrengthSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Memory Strength", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(
                "Fresh" to ("🌱" to 0.4f),
                "Growing" to ("🌿" to 0.7f),
                "Strong" to ("🌳" to 0.9f)
            ).forEach { (label, iconData) ->
                val (icon, strength) = iconData
                ModernCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = SurfaceLight
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(icon, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { strength },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = PrimaryPurple,
                            trackColor = PrimaryPurple.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ForgettingCurveChart() {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        val width = size.width
        val height = size.height
        
        // Axis
        drawLine(Color.Gray.copy(alpha = 0.3f), Offset(0f, 0f), Offset(0f, height), strokeWidth = 2.dp.toPx())
        drawLine(Color.Gray.copy(alpha = 0.3f), Offset(0f, height), Offset(width, height), strokeWidth = 2.dp.toPx())

        // Natural Forgetting (Exponential decay)
        val decayPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, height * 0.1f)
            cubicTo(width * 0.2f, height * 0.5f, width * 0.5f, height * 0.9f, width, height * 0.95f)
        }
        drawPath(decayPath, Color.Gray.copy(alpha = 0.4f), style = androidx.compose.ui.graphics.drawscope.Stroke(4.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f))))

        // SR Boosts (Peaks)
        val srPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, height * 0.1f)
            // Review 1
            lineTo(width * 0.2f, height * 0.3f)
            quadraticTo(width * 0.22f, height * 0.05f, width * 0.25f, height * 0.05f)
            // Decay 2
            lineTo(width * 0.5f, height * 0.2f)
            quadraticTo(width * 0.52f, height * 0.05f, width * 0.55f, height * 0.05f)
            // Decay 3
            lineTo(width * 0.85f, height * 0.15f)
            quadraticTo(width * 0.87f, height * 0.05f, width * 1f, height * 0.05f)
        }
        drawPath(srPath, PrimaryPurple, style = androidx.compose.ui.graphics.drawscope.Stroke(6.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
        
        // Area fill for peaks
        val fillPath = androidx.compose.ui.graphics.Path().apply {
            addPath(srPath)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(fillPath, Brush.verticalGradient(listOf(PrimaryPurple.copy(alpha = 0.2f), Color.Transparent)))
    }
}

@Composable
private fun ChartSection(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            subtitle?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
        ModernCard(backgroundColor = SurfaceLight) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ChartSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        ModernCard(backgroundColor = SurfaceLight) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun WeeklyBarChart() {
    val data = listOf(45, 30, 60, 20, 50, 40, 35)
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val maxVal = data.maxOrNull()?.toFloat() ?: 1f

    Row(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, value ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    // Track
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SilverAccent.copy(alpha = 0.1f))
                    )
                    // Progress
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .fillMaxHeight(value / (maxVal * 1.1f))
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.verticalGradient(listOf(PrimaryPurple, PrimaryBlue)))
                    )
                }
                Text(days[index], style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun GrowthLineChart() {
    // Simple line visualization using Canvas
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val points = listOf(0.2f, 0.3f, 0.25f, 0.5f, 0.45f, 0.8f, 0.9f)
        val path = androidx.compose.ui.graphics.Path()
        val width = size.width
        val height = size.height
        val stepX = width / (points.size - 1)

        points.forEachIndexed { index, point ->
            val x = index * stepX
            val y = height - (point * height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = PrimaryBlue,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
        
        // Fill area under the path
        val fillPath = androidx.compose.ui.graphics.Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(listOf(PrimaryBlue.copy(alpha = 0.2f), Color.Transparent))
        )
    }
}

@Composable
private fun ActivityHeatmap() {
    val weeks = 12
    val days = 7
    androidx.compose.foundation.lazy.LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(weeks) { week ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(days) { day ->
                    val intensity = (0..4).random()
                    val color = when(intensity) {
                        0 -> SilverAccent.copy(alpha = 0.1f)
                        1 -> Success.copy(alpha = 0.2f)
                        2 -> Success.copy(alpha = 0.4f)
                        3 -> Success.copy(alpha = 0.7f)
                        else -> Success
                    }
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
fun MasteryDonutChart(percentage: Float) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            
            // Background
            drawArc(
                color = Success.copy(alpha = 0.1f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )
            
            // Foreground
            drawArc(
                brush = Brush.sweepGradient(listOf(Success, Success.copy(alpha = 0.6f), Success)),
                startAngle = -90f,
                sweepAngle = 360f * percentage,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
        }
        Text(
            text = "${(percentage * 100).toInt()}%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
