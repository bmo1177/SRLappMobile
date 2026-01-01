package com.example.srlappexperiment.presentation.ui.screens.placement

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard
import com.example.srlappexperiment.ui.components.PremiumGradientProgressBar

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.srlappexperiment.presentation.viewmodel.placement.PlacementViewModel
import com.example.srlappexperiment.presentation.viewmodel.placement.PlacementUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementTestScreen(
    viewModel: PlacementViewModel = hiltViewModel(),
    onComplete: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    val totalQuestions = placementQuestions.size
    val progress = (currentQuestionIndex + 1).toFloat() / totalQuestions
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is PlacementUiState.Success) {
            // Assessment Results
            val level = when {
                score >= 9 -> "B2 (Upper Intermediate)"
                score >= 6 -> "B1 (Intermediate)"
                score >= 3 -> "A2 (Elementary)"
                else -> "A1 (Beginner)"
            }
            onComplete(level)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Placement Test", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PremiumGradientProgressBar(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            if (currentQuestionIndex < totalQuestions) {
                val question = placementQuestions[currentQuestionIndex]
                
                Text(
                    text = "Question ${currentQuestionIndex + 1} of $totalQuestions",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = question.text,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    question.options.forEach { option ->
                        ModernCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (option == question.correctAnswer) {
                                        score++
                                    }
                                    currentQuestionIndex++
                                },
                            backgroundColor = SurfaceLight
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.padding(20.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            } else {
                // Assessment Results
                val level = when {
                    score >= 9 -> "B2 (Upper Intermediate)"
                    score >= 6 -> "B1 (Intermediate)"
                    score >= 3 -> "A2 (Elementary)"
                    else -> "A1 (Beginner)"
                }
                
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Assessment Complete!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your starting level is:", color = TextSecondary)
                    Text(level, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryPurple)
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Button(
                        onClick = { 
                            val assessedLevel = when {
                                score >= 9 -> "B2"
                                score >= 6 -> "B1"
                                score >= 3 -> "A2"
                                else -> "A1"
                            }
                            viewModel.saveResult(assessedLevel) 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(16.dp),
                        enabled = uiState !is PlacementUiState.Loading
                    ) {
                        if (uiState is PlacementUiState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Start My Journey", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                    
                    if (uiState is PlacementUiState.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = (uiState as PlacementUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

data class PlacementQuestion(val text: String, val options: List<String>, val correctAnswer: String)

val placementQuestions = listOf(
    PlacementQuestion("How do you say 'Hello' in Spanish?", listOf("Adiós", "Hola", "Gracias", "Por favor"), "Hola"),
    PlacementQuestion("Which word means 'House'?", listOf("Carro", "Perro", "Casa", "Libro"), "Casa"),
    PlacementQuestion("Translate: 'I am from Spain'", listOf("Soy de España", "Estoy en España", "Voy a España", "Tengo España"), "Soy de España"),
    // Add more questions to reach 10...
    PlacementQuestion("What is the plural of 'el niño'?", listOf("los niños", "las niñas", "el niños", "los niño"), "los niños"),
    PlacementQuestion("Translate: 'She is eating an apple'", listOf("Ella come una manzana", "Ella come un manzana", "Él come una manzana", "Ellas comen manzanas"), "Ella come una manzana"),
    PlacementQuestion("Which verb means 'To be' (permanent state)?", listOf("Estar", "Ser", "Hacer", "Tener"), "Ser"),
    PlacementQuestion("How do you say 'Yesterday'?", listOf("Hoy", "Mañana", "Ayer", "Ahora"), "Ayer"),
    PlacementQuestion("Translate: 'We have two brothers'", listOf("Tenemos dos hermanos", "Tienen dos hermanos", "Tenéis dos hermanos", "Tengo dos hermanos"), "Tenemos dos hermanos"),
    PlacementQuestion("What is 'Purple' in Spanish?", listOf("Rojo", "Azul", "Morado", "Verde"), "Morado"),
    PlacementQuestion("Translate: 'It's cold today'", listOf("Hace frío hoy", "Hace calor hoy", "Está lloviendo hoy", "Hay sol hoy"), "Hace frío hoy")
)
