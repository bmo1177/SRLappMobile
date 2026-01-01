package com.example.srlappexperiment.presentation.ui.screens.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.presentation.ui.components.game.LocalLeaderboard
import com.example.srlappexperiment.presentation.ui.components.game.QuestionCard
import com.example.srlappexperiment.presentation.ui.components.game.ScoreBoard
import com.example.srlappexperiment.presentation.ui.components.game.TimerDisplay
import com.example.srlappexperiment.presentation.viewmodel.game.FocusChallengeViewModel
import com.example.srlappexperiment.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusChallengeScreen(
    viewModel: FocusChallengeViewModel,
    onBack: () -> Unit
) {
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val score by viewModel.score.collectAsState()
    val accuracy by viewModel.accuracy.collectAsState()
    val combo by viewModel.combo.collectAsState()
    val isGameOver by viewModel.isGameOver.collectAsState()
    val result by viewModel.gameResult.collectAsState()
    val personalBest by viewModel.personalBest.collectAsState()

    var gameStarted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Focus Challenge", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!gameStarted) {
                // Pre-game Setup / Lobby
                GameLobby(
                    personalBest = personalBest,
                    onStart = { diff ->
                        gameStarted = true
                        viewModel.startGame(diff)
                    }
                )
            } else if (isGameOver) {
                // Results Screen
                GameResultsScreen(
                    score = score,
                    accuracy = accuracy,
                    onPlayAgain = {
                        gameStarted = false
                        viewModel.startGame("beginner") // Default to beginner for restart choice or keep current
                    },
                    onExit = onBack
                )
            } else {
                // Active Game
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScoreBoard(score = score, accuracy = accuracy, combo = combo)
                    Spacer(Modifier.height(40.dp))
                    TimerDisplay(seconds = timeRemaining)
                    Spacer(Modifier.height(40.dp))
                    
                    currentQuestion?.let {
                        QuestionCard(
                            question = it,
                            onOptionSelected = { index ->
                                viewModel.selectAnswer(index)
                            }
                        )
                    } ?: CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun GameLobby(
    personalBest: Int,
    onStart: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Ready for the Challenge?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "90 seconds. 30 questions. Maximize your score and accuracy!",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(Modifier.height(48.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.1f))
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Your Best", style = MaterialTheme.typography.labelMedium)
                Text(personalBest.toString(), fontSize = 48.sp, fontWeight = FontWeight.Black, color = TealPrimary)
            }
        }

        Spacer(Modifier.height(48.dp))

        Text("Select Difficulty", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DifficultyButton("Beginner", Color(0xFF4CAF50)) { onStart("beginner") }
            DifficultyButton("Intermediate", Color(0xFFFF9800)) { onStart("intermediate") }
            DifficultyButton("Advanced", Color(0xFFF44336)) { onStart("advanced") }
        }
    }
}

@Composable
fun DifficultyButton(label: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(label.first().toString())
    }
}

@Composable
fun GameResultsScreen(
    score: Int,
    accuracy: Float,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Timed Out!", fontSize = 32.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(32.dp))
        
        ResultStat("Final Score", score.toString(), TealPrimary, isScore = true)
        
        Spacer(Modifier.height(24.dp))
        
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = accuracy / 100f,
                modifier = Modifier.size(120.dp),
                color = Color(0xFF4CAF50),
                strokeWidth = 12.dp,
                trackColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
            )
            ResultStat("Accuracy", "${accuracy.toInt()}%", Color(0xFF4CAF50))
        }
        
        Spacer(Modifier.height(48.dp))
        
        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.PlayArrow, null)
            Spacer(Modifier.width(8.dp))
            Text("Play Again", fontWeight = FontWeight.Bold)
        }
        
        Spacer(Modifier.height(16.dp))
        
        TextButton(onClick = onExit) {
            Text("Go to Dashboard", color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun ResultStat(label: String, value: String, color: Color, isScore: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
        if (isScore) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, fontSize = 64.sp, fontWeight = FontWeight.Black, color = color)
        } else {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
