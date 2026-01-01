package com.example.srlappexperiment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.srlappexperiment.presentation.ui.navigation.NavGraph
import com.example.srlappexperiment.presentation.ui.navigation.Routes
import com.example.srlappexperiment.presentation.viewmodel.MainViewModel
import com.example.srlappexperiment.ui.theme.SRLAppExperimentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val themeState by mainViewModel.themeState.collectAsState()
            
            val isDark = if (themeState.useSystemTheme) {
                androidx.compose.foundation.isSystemInDarkTheme()
            } else {
                themeState.isDarkMode
            }

            SRLAppExperimentTheme(darkTheme = isDark) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(
                        startDestination = Routes.SPLASH
                    )
                }
            }
        }
    }
}