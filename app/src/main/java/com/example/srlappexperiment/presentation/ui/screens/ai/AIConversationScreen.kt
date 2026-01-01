package com.example.srlappexperiment.presentation.ui.screens.ai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIConversationScreen(
    onNavigateBack: () -> Unit
) {
    var isListening by remember { mutableStateOf(false) }
    var aiStatus by remember { mutableStateOf("Ready to chat") }
    var transcript by remember { mutableStateOf("Hello! I'm your AI partner. Tap the mic to start speaking.") }
    
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Partner", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Black // Base layer
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0F0C29),
                            Color(0xFF302B63),
                            Color(0xFF24243E)
                        )
                    )
                )
                .padding(padding)
        ) {
            // Background Dynamic Ambient Glow
            AmbientGlow(waveOffset)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // AI Status & Avatar
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AIAvatar(isListening)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = aiStatus,
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Transcript & Feedback Area
                PronunciationFeedbackArea(
                    isProcessing = !isListening && aiStatus == "Thinking...",
                    transcript = transcript
                )

                // Waveform visualization
                AnimatedVisibility(
                    visible = isListening,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    VoiceWaveform()
                }

                // Controls
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { /* TODO: Toggle Camera/Vision */ },
                        modifier = Modifier.size(56.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                    }
                    
                    Spacer(modifier = Modifier.width(24.dp))
                    
                    LargeMicButton(
                        isListening = isListening,
                        onClick = { 
                            isListening = !isListening
                            aiStatus = if (isListening) "I'm listening..." else "Thinking..."
                            if (!isListening) {
                                // Simulate processing
                                transcript = "You: Bonjour, comment ça va?\nAI: Ça va très bien! Your pronunciation was 92% accurate."
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    IconButton(
                        onClick = { /* TODO: Switch Language */ },
                        modifier = Modifier.size(56.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AIAvatar(isListening: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(contentAlignment = Alignment.Center) {
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                    .background(PrimaryPurple.copy(alpha = 0.2f), CircleShape)
            )
        }
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = PrimaryPurple,
            tonalElevation = 8.dp
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.padding(20.dp).size(40.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun LargeMicButton(isListening: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(88.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isListening) Error else Color.White,
            contentColor = if (isListening) Color.White else PrimaryPurple
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
    ) {
        Icon(
            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = "Mic",
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun VoiceWaveform() {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        for (i in 0 until 5) {
            val path = androidx.compose.ui.graphics.Path()
            path.moveTo(0f, centerY)
            
            val amplitude = (20..50).random().dp.toPx() * (1f - (i * 0.2f))
            val freq = 2f + (i * 0.5f)
            
            for (x in 0..width.toInt() step 5) {
                val y = centerY + amplitude * sin(freq * Math.PI * x / width + phase + i).toFloat()
                path.lineTo(x.toFloat(), y)
            }

            drawPath(
                path = path,
                color = if (i % 2 == 0) PrimaryPurple.copy(alpha = 0.4f) else PrimaryBlue.copy(alpha = 0.4f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PronunciationFeedbackArea(isProcessing: Boolean, transcript: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp),
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (isProcessing) {
                CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Analyzing pronunciation...", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelMedium)
            } else {
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val words = transcript.split(" ")
                    words.forEach { word ->
                        // Mock confidence: Some words are green, some yellow
                        val color = when {
                            word.length > 7 -> Success // Long words are green (mock)
                            word.contains("o", ignoreCase = true) -> AccentCoral // Words with 'o' are yellow
                            else -> Color.White
                        }
                        Text(
                            text = "$word ",
                            style = MaterialTheme.typography.headlineSmall,
                            color = color,
                            fontWeight = if (color != Color.White) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                
                if (transcript.contains("AI:")) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Keep it up! Try to emphasize the 'R' sounds more naturally.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryBlue,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun AmbientGlow(offset: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(PrimaryPurple.copy(alpha = 0.1f), Color.Transparent),
                center = Offset(size.width * (0.5f + 0.2f * sin(offset)), size.height * 0.3f),
                radius = size.width * 0.8f
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(PrimaryBlue.copy(alpha = 0.1f), Color.Transparent),
                center = Offset(size.width * (0.5f - 0.2f * sin(offset + 1)), size.height * 0.7f),
                radius = size.width * 0.8f
            )
        )
    }
}
