package com.example.srlappexperiment.presentation.ui.screens.vocabulary

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.srlappexperiment.presentation.ui.components.vocabulary.AiExplanationCard
import com.example.srlappexperiment.presentation.ui.components.vocabulary.AiExplanationShimmer
import com.example.srlappexperiment.presentation.viewmodel.VocabularyPracticeViewModel
import com.example.srlappexperiment.ui.theme.*

/**
 * Main vocabulary practice screen
 * Displays flashcards, handles rating input, and shows session summary
 * 
 * @param viewModel ViewModel for managing practice state
 * @param onSessionComplete Callback when session ends
 * @param onNavigateBack Callback to navigate back
 */
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VocabularyPracticeScreen(
    viewModel: VocabularyPracticeViewModel = hiltViewModel(),
    onSessionComplete: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val cards by viewModel.sessionCards.collectAsState()
    val sessionStats by viewModel.sessionStats.collectAsState()
    val sessionProgress by viewModel.sessionProgress.collectAsState()
    val isSessionComplete by viewModel.isSessionComplete.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRatingEnabled by viewModel.isRatingEnabled.collectAsState()
    val noCardsAvailable by viewModel.noCardsAvailable.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val aiExplanation by viewModel.aiExplanation.collectAsState()
    val isLoadingExplanation by viewModel.isLoadingExplanation.collectAsState()
    
    var isCardFlipped by remember { mutableStateOf(false) }
    var showAiHelp by remember { mutableStateOf(false) }
    var lowPressureMode by remember { mutableStateOf(false) }
    var streakCount by remember { mutableStateOf(0) }
    var showCelebration by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // Pager state
    val pagerState = rememberPagerState(pageCount = { cards.size })
    
    // Sync pager with ViewModel index
    LaunchedEffect(sessionProgress.current) {
        if (sessionProgress.current < cards.size && pagerState.currentPage != sessionProgress.current) {
            pagerState.animateScrollToPage(sessionProgress.current)
        }
    }
    
    // Show error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    // Reset flip state and AI help when card changes
    LaunchedEffect(pagerState.currentPage) {
        isCardFlipped = false
        showAiHelp = false
        viewModel.clearAiExplanation()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vocabulary Practice",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    if (!isSessionComplete && !noCardsAvailable) {
                        IconButton(onClick = { lowPressureMode = !lowPressureMode }) {
                            Icon(
                                imageVector = if (lowPressureMode) Icons.Default.TimerOff else Icons.Default.Timer,
                                contentDescription = "Toggle Pressure Mode",
                                tint = if (lowPressureMode) PrimaryBlue else PrimaryPurple
                            )
                        }
                        IconButton(onClick = { viewModel.endSession() }) {

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "End session"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (!isSessionComplete && !noCardsAvailable && isCardFlipped && !showAiHelp) {
                FloatingActionButton(
                    onClick = {
                        showAiHelp = true
                        viewModel.getAiExplanation()
                    },
                    containerColor = PrimaryPurple,
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Get AI Help"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Help")
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    LoadingState()
                }
                noCardsAvailable -> {
                    NoCardsState()
                }
                isSessionComplete -> {
                    SessionSummaryCard(
                        stats = sessionStats,
                        onContinue = onSessionComplete,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        PracticeContent(
                            cards = cards,
                            pagerState = pagerState,
                            sessionProgress = sessionProgress,
                            isCardFlipped = isCardFlipped,
                            isRatingEnabled = isRatingEnabled,
                            isLowPressure = lowPressureMode,
                            onFlipCard = { isCardFlipped = !isCardFlipped },
                            onRating = { quality ->
                                if (quality >= 3) {
                                    streakCount++
                                    if (streakCount % 5 == 0) showCelebration = true
                                } else {
                                    streakCount = 0
                                }
                                viewModel.reviewCard(quality)
                            }
                        )
                        
                        // AI Explanation overlay
                        if (showAiHelp) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isLoadingExplanation -> {
                                        AiExplanationShimmer()
                                    }
                                    aiExplanation != null -> {
                                        AiExplanationCard(
                                            explanation = aiExplanation!!,
                                            onDismiss = {
                                                showAiHelp = false
                                                viewModel.clearAiExplanation()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Streak Celebration Overlay
            if (showCelebration) {
                LaunchedEffect(Unit) {
                    delay(1500)
                    showCelebration = false
                }
                StreakCelebration(streakCount)
            }
        }
    }
}

@Composable
private fun StreakCelebration(count: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔥", fontSize = 80.sp)
            Text(
                text = "$count STREAK!",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = PrimaryPurple
            )
            Text(
                text = "In the zone! Your brain is firing! 🧠✨",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
        }
    }
}


/**
 * Main practice content with header, flashcard pager, and rating buttons
 */
@Composable
private fun PracticeContent(
    cards: List<com.example.srlappexperiment.data.local.database.entities.VocabularyCard>,
    pagerState: PagerState,
    sessionProgress: com.example.srlappexperiment.domain.model.SessionProgress,
    isCardFlipped: Boolean,
    isRatingEnabled: Boolean,
    isLowPressure: Boolean,
    onFlipCard: () -> Unit,
    onRating: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            SessionHeader(progress = sessionProgress, isLowPressure = isLowPressure)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp,
            userScrollEnabled = false // Control via buttons for SR consistency
        ) { pageIndex ->
            val card = cards[pageIndex]
            FlashcardWidget(
                card = card,
                isFlipped = if (pagerState.currentPage == pageIndex) isCardFlipped else false,
                onFlip = { if (pagerState.currentPage == pageIndex) onFlipCard() },
                modifier = Modifier.fillMaxHeight()
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            if (isCardFlipped) {
                RatingButtons(
                    onRating = onRating,
                    enabled = isRatingEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Tap to reveal answer",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SessionHeader(
    progress: com.example.srlappexperiment.domain.model.SessionProgress,
    isLowPressure: Boolean
) {
    val isFocusMode = !isLowPressure
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isFocusMode) "Focus Challenge" else "Zen Practice",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isFocusMode) TextPrimary else PrimaryBlue
                )
                Text(
                    text = if (isFocusMode) "Don't break your concentration!" else "Learning at your own pace",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            
            if (isFocusMode) {
                com.example.srlappexperiment.presentation.ui.components.game.TimerDisplay(seconds = 300) // Mock 5 mins
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LinearProgressIndicator(
                progress = { progress.progressFraction },
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = PrimaryPurple,
                trackColor = PrimaryPurple.copy(alpha = 0.1f),
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
            Text(
                text = "${progress.current}/${progress.total}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryPurple
            )
        }
    }
}

/**
 * Loading state while fetching cards
 */
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = PrimaryPurple,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Loading your cards...",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * Empty state when no cards are available for review
 */
@Composable
private fun NoCardsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "All caught up!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Come back tomorrow for more vocabulary practice.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Great job keeping up with your studies! 🎉",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
