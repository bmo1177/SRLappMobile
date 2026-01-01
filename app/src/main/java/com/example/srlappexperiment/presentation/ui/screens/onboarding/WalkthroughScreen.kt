package com.example.srlappexperiment.presentation.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.R
import com.example.srlappexperiment.ui.theme.PrimaryPurple
import kotlinx.coroutines.launch

@Composable
fun WalkthroughScreen(
    onFinished: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()

    val slides = listOf(
        WalkthroughSlide(
            "Master Any Language, Your Way",
            "AI-powered lessons that adapt to your pace. Learn faster than traditional methods.",
            R.drawable.onboarding_welcome
        ),
        WalkthroughSlide(
            "Learn Smarter, Not Harder",
            "Our AI remembers what you forget. Review words exactly when your brain needs refreshing.",
            R.drawable.onboarding_goals // Using goals for SRS metaphor as placeholder
        ),
        WalkthroughSlide(
            "Practice Real Conversations",
            "Talk with our AI tutor anytime. Get instant pronunciation feedback and build confidence.",
            R.drawable.onboarding_ai
        ),
        WalkthroughSlide(
            "Make Progress, Earn Rewards",
            "Build streaks, unlock achievements, and watch your vocabulary grow.",
            R.drawable.onboarding_success
        ),
        WalkthroughSlide(
            "Learn Anywhere, Even Offline",
            "Download lessons for offline access. Learn on flights or anywhere without internet.",
            R.drawable.onboarding_success // Reusing success for offline for now
        )
    )

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator
                Row(
                    Modifier
                        .height(30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { iteration ->
                        val color = if (pagerState.currentPage == iteration) PrimaryPurple else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(8.dp)
                                .padding(2.dp)
                                .align(Alignment.CenterVertically)
                                .padding(2.dp)
                                // Simplified indicator
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < 4) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinished()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text(if (pagerState.currentPage < 4) "Continue" else "Get Started")
                }

                TextButton(onClick = onNavigateToLogin) {
                    Text("I already have an account", color = PrimaryPurple)
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            WalkthroughSlideContent(slides[page])
        }
    }
}

@Composable
fun WalkthroughSlideContent(slide: WalkthroughSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = slide.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 32.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = slide.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = slide.description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 24.sp
        )
    }
}

data class WalkthroughSlide(
    val title: String,
    val description: String,
    val imageRes: Int
)
