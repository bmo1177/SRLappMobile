package com.example.srlappexperiment.presentation.ui.components.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard
import com.example.srlappexperiment.presentation.ui.components.StreakCounter
import com.example.srlappexperiment.domain.model.HeatmapCell
import com.example.srlappexperiment.domain.model.DailyStat

@Composable
fun StreakWidget(streak: Int, longest: Int, modifier: Modifier = Modifier) {
    ModernCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = PrimaryPurple.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StreakCounter(count = streak)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Daily Streak",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Personal Best: $longest days",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ModernCard(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Image(
                imageVector = icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(PrimaryBlue),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
fun LineChart(
    data: List<DailyStat>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val maxMinutes = 60f
        val width = size.width
        val height = size.height
        val spacing = width / (data.size - 1).coerceAtLeast(1)

        val path = Path()
        data.forEachIndexed { index, stat ->
            val x = index * spacing
            val y = height - (stat.minutesStudied.toFloat() / maxMinutes * height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = PrimaryPurple,
            style = Stroke(width = 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
    }
}

@Composable
fun HeatmapChart(data: List<HeatmapCell>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Activity Heatmap", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Simple grid for heatmap (7x4 approx)
            (0 until 28).chunked(7).forEach { week ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    week.forEach { dayIdx ->
                        val cellData = data.getOrNull(dayIdx)
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = getHeatmapColor(cellData?.level ?: 0),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

private fun getHeatmapColor(level: Int): Color {
    return when (level) {
        1 -> PrimaryPurple.copy(alpha = 0.2f)
        2 -> PrimaryPurple.copy(alpha = 0.5f)
        3 -> PrimaryPurple.copy(alpha = 0.8f)
        4 -> PrimaryPurple
        else -> SilverAccent.copy(alpha = 0.3f)
    }
}

@Composable
fun SpacedRepetitionQueueWidget(
    dueCount: Int,
    modifier: Modifier = Modifier
) {
    val (statusText, statusColor) = when {
        dueCount == 0 -> "Optional" to Success
        dueCount < 10 -> "Due Soon" to Warning
        else -> "Overdue" to Error
    }

    ModernCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = SurfaceLight
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(statusColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(statusColor),
                        modifier = Modifier.size(24.dp)
                    )
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "$dueCount words need review",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            // Urgency Indicator dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(statusColor)
            )
        }
    }
}



@Composable
fun SmartNudgeWidget(
    message: String,
    timeLabel: String,
    modifier: Modifier = Modifier
) {
    ModernCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = PrimaryBlue.copy(alpha = 0.08f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(PrimaryBlue),
                    modifier = Modifier.size(20.dp)
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "AI Prediction: $timeLabel",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryBlue
                )
            }
        }
    }
}
