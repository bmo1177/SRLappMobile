package com.example.srlappexperiment.presentation.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.srlappexperiment.R
import com.example.srlappexperiment.ui.components.GradientStyle
import com.example.srlappexperiment.ui.components.premiumBackground
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.srlappexperiment.presentation.viewmodel.MainViewModel

@Composable
fun SplashScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val isLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val isOnboarded by viewModel.isOnboardingComplete.collectAsState()
    
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        
        val route = when {
            isLoggedIn && isOnboarded -> "dashboard"
            isLoggedIn && !isOnboarded -> "onboarding"
            else -> "walkthrough"
        }
        onNavigate(route)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .premiumBackground(GradientStyle.PurpleBlue),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .scale(scale)
                .alpha(alpha)
        )
    }
}
