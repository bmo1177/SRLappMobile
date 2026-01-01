package com.example.srlappexperiment.presentation.ui.screens.roadmap

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.domain.model.LearningSession
import com.example.srlappexperiment.domain.model.SessionStatus
import com.example.srlappexperiment.domain.model.RoadmapSessionType
import com.example.srlappexperiment.ui.theme.*

/**
 * A single interactive node in the learning roadmap
 */
@Composable
fun RoadmapNode(
    session: LearningSession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = session.status
    
    // Animations for current node
    val infiniteTransition = rememberInfiniteTransition(label = "node_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (status == SessionStatus.CURRENT) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val nodeColor = when (status) {
        SessionStatus.LOCKED -> Color.LightGray
        SessionStatus.AVAILABLE -> GoldAccent
        SessionStatus.CURRENT -> PrimaryPurple
        SessionStatus.COMPLETED -> Success
    }
    
    val iconColor = when (status) {
        SessionStatus.LOCKED -> Color.Gray
        else -> Color.White
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .clickable(enabled = status != SessionStatus.LOCKED, onClick = onClick)
    ) {
        // Crown for completed/milestone
        if (session.type == RoadmapSessionType.MILESTONE && status != SessionStatus.LOCKED) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier
                    .size(24.dp)
                    .offset(y = 8.dp)
            )
        }
        
        // Main circle node
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(
                    if (status == SessionStatus.LOCKED) {
                        Color.LightGray.copy(alpha = 0.3f)
                    } else {
                        nodeColor
                    }
                )
                .border(
                    width = 4.dp,
                    color = if (status == SessionStatus.CURRENT) GoldAccent else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            // Inner ring/progress
            if (status == SessionStatus.CURRENT) {
                CircularProgressIndicator(
                    progress = { 0.6f }, // Mock progress
                    modifier = Modifier.fillMaxSize(),
                    color = GoldAccent,
                    trackColor = Color.Transparent,
                )
            }
            
            // Icon
            Icon(
                imageVector = getSessionIcon(session.type, status),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Label
        if (status == SessionStatus.CURRENT || status == SessionStatus.AVAILABLE) {
            Surface(
                color = nodeColor,
                shape = MaterialTheme.shapes.small,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = session.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Draws a curved path connecting two nodes
 */
@Composable
fun RoadmapPath(
    startX: Dp,
    startY: Dp,
    endX: Dp,
    endY: Dp,
    isCompleted: Boolean
) {
    val density = LocalDensity.current
    val strokeWidth = with(density) { 6.dp.toPx() }
    val color = if (isCompleted) GoldAccent else Color.LightGray.copy(alpha = 0.5f)
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val start = Offset(startX.toPx(), startY.toPx())
        val end = Offset(endX.toPx(), endY.toPx())
        
        val controlPoint1 = Offset(start.x, start.y + (end.y - start.y) / 2)
        val controlPoint2 = Offset(end.x, start.y + (end.y - start.y) / 2)
        
        val path = Path().apply {
            moveTo(start.x, start.y)
            cubicTo(
                controlPoint1.x, controlPoint1.y,
                controlPoint2.x, controlPoint2.y,
                end.x, end.y
            )
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                pathEffect = if (!isCompleted) PathEffect.dashPathEffect(floatArrayOf(20f, 20f)) else null
            )
        )
    }
}

private fun getSessionIcon(type: RoadmapSessionType, status: SessionStatus): ImageVector {
    if (status == SessionStatus.LOCKED) return Icons.Default.Lock
    if (status == SessionStatus.COMPLETED) return Icons.Default.Check
    
    return when (type) {
        RoadmapSessionType.VOCABULARY -> Icons.Default.Translate
        RoadmapSessionType.GRAMMAR -> Icons.Default.Edit
        RoadmapSessionType.LISTENING -> Icons.Default.Headphones
        RoadmapSessionType.QUIZ -> Icons.Default.Quiz
        RoadmapSessionType.REVIEW -> Icons.Default.History
        RoadmapSessionType.MILESTONE -> Icons.Default.EmojiEvents
    }
}
