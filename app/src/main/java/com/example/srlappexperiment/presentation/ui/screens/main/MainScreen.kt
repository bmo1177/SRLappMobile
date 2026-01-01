package com.example.srlappexperiment.presentation.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.srlappexperiment.R
import com.example.srlappexperiment.presentation.ui.navigation.Routes
import com.example.srlappexperiment.presentation.ui.screens.dashboard.DashboardScreen
import com.example.srlappexperiment.presentation.ui.screens.roadmap.LearningRoadmapScreen
import com.example.srlappexperiment.presentation.ui.screens.vocabulary.VocabularyLibraryScreen
import com.example.srlappexperiment.presentation.ui.screens.dashboard.AnalyticsScreen
import com.example.srlappexperiment.presentation.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String, val label: String, val icon: Int) {
    object Home : Screen(Routes.DASHBOARD, "Home", R.drawable.ic_nav_home)
    object Learn : Screen(Routes.LEARNING_ROADMAP, "Learn", R.drawable.ic_content_vocab)
    object Practice : Screen(Routes.VOCABULARY_PRACTICE, "Practice", R.drawable.ic_learning_flashcard)
    object Progress : Screen(Routes.ANALYTICS, "Progress", R.drawable.ic_progress_trophy)
    object Profile : Screen(Routes.PROFILE, "Me", R.drawable.ic_nav_profile)
}

@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToAiPartner: () -> Unit,
    onNavigateToPlacement: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToSupport: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Learn,
        Screen.Practice,
        Screen.Progress,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(ImageVector.vectorResource(id = screen.icon), contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onNavigateToPractice = { navController.navigate(Routes.LEARNING_ROADMAP) },
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToLibrary = { navController.navigate(Routes.VOCABULARY) },
                    onNavigateToSummary = { navController.navigate(Routes.ANALYTICS) },
                    onNavigateToAiPartner = onNavigateToAiPartner,
                    onNavigateToPlacement = onNavigateToPlacement,
                    onNavigateToSubscription = onNavigateToSubscription
                )
            }
            composable(Routes.LEARNING_ROADMAP) {
                com.example.srlappexperiment.presentation.ui.screens.curriculum.CurriculumScreen(
                    onNavigateToLesson = { lessonId -> 
                        navController.navigate(Routes.VOCABULARY_PRACTICE) 
                    }
                )
            }
            composable(Routes.VOCABULARY_PRACTICE) {
                com.example.srlappexperiment.presentation.ui.screens.practice.PracticeHubScreen(
                    onStartReview = { isQuick -> 
                        // Logic for quick/full
                        navController.navigate("lesson_flow") 
                    },
                    onViewLibrary = { navController.navigate(Routes.VOCABULARY) }
                )
            }
            // Adding a distinct route for the lesson flow vs the hub
            composable("lesson_flow") {
                com.example.srlappexperiment.presentation.ui.screens.lesson.LessonFlowScreen(
                    onFinished = { navController.popBackStack() }
                )
            }
            composable(Routes.ANALYTICS) {
                AnalyticsScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Routes.PROFILE) {
                // Settings acts as Profile for now
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToAdmin = onNavigateToAdmin,
                    onNavigateToSupport = onNavigateToSupport,
                    onNavigateToSubscription = onNavigateToSubscription,
                    onLogout = { /* Handled at top level */ }
                )
            }
            composable(Routes.VOCABULARY) {
                VocabularyLibraryScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
