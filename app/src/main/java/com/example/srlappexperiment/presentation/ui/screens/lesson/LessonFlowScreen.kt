package com.example.srlappexperiment.presentation.ui.screens.lesson

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.srlappexperiment.presentation.ui.screens.vocabulary.VocabularyPracticeScreen
import com.example.srlappexperiment.ui.theme.PrimaryPurple

enum class LessonStep {
    INTRO, VOCAB, AUDIO, CONVERSATION, QUIZ, RESULTS
}

@Composable
fun LessonFlowScreen(
    onFinished: () -> Unit
) {
    var currentStep by remember { mutableStateOf(LessonStep.INTRO) }
    var progress by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            if (currentStep != LessonStep.RESULTS) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = PrimaryPurple,
                    trackColor = PrimaryPurple.copy(alpha = 0.1f)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Crossfade(targetState = currentStep, label = "lesson_step") { step ->
                when (step) {
                    LessonStep.INTRO -> LessonIntroScreen { 
                        currentStep = LessonStep.VOCAB
                        progress = 0.2f
                    }
                    LessonStep.VOCAB -> VocabularyPracticeScreen(
                        onSessionComplete = { 
                            currentStep = LessonStep.AUDIO
                            progress = 0.4f
                        },
                        onNavigateBack = onFinished
                    )
                    LessonStep.AUDIO -> LessonAudioPracticeScreen { 
                        currentStep = LessonStep.CONVERSATION
                        progress = 0.6f
                    }
                    LessonStep.CONVERSATION -> LessonConversationScreen { 
                        currentStep = LessonStep.QUIZ
                        progress = 0.8f
                    }
                    LessonStep.QUIZ -> LessonQuizScreen { 
                        currentStep = LessonStep.RESULTS
                        progress = 1.0f
                    }
                    LessonStep.RESULTS -> LessonResultsScreen { onFinished() }
                }
            }
        }
    }
}

@Composable fun LessonIntroScreen(onStart: () -> Unit) { 
    Column(Modifier.fillMaxSize(), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Lesson 7: At the Restaurant", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onStart) { Text("Start Lesson") }
    }
}
@Composable 
fun LessonAudioPracticeScreen(onComplete: () -> Unit) { 
    com.example.srlappexperiment.presentation.ui.screens.lesson.components.ListeningExercise(
        phrase = "I would like the menu, please",
        translation = "Me gustaría el menú, por favor",
        onNext = onComplete
    )
}

@Composable 
fun LessonConversationScreen(onComplete: () -> Unit) { 
    com.example.srlappexperiment.presentation.ui.screens.lesson.components.DialogueExercise(
        scenario = "At the Restaurant",
        dialogues = listOf(
            "Waiter" to "Welcome! Here is the menu.",
            "You" to "Thank you. What is the special today?",
            "Waiter" to "The salmon is excellent.",
            "You" to "Great, I will have the salmon."
        ),
        onComplete = onComplete
    )
}
@Composable fun LessonQuizScreen(onComplete: () -> Unit) { 
    Column(Modifier.fillMaxSize(), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Quiz: Order at the Restaurant", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onComplete) { Text("Submit Quiz") }
    }
}
@Composable fun LessonResultsScreen(onClose: () -> Unit) { 
    Column(Modifier.fillMaxSize(), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Lesson Complete! 🎉", style = MaterialTheme.typography.headlineMedium)
        Text("Exp Gained: +50 XP", color = com.example.srlappexperiment.ui.theme.Success)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onClose) { Text("Back to Dashboard") }
    }
}
