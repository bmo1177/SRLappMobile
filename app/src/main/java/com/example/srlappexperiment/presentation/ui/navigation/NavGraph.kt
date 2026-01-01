package com.example.srlappexperiment.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.srlappexperiment.presentation.ui.screens.vocabulary.VocabularyPracticeScreen
import com.example.srlappexperiment.presentation.ui.screens.dashboard.DashboardScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.srlappexperiment.presentation.ui.screens.settings.SettingsScreen

/**
 * Navigation routes for the app
 */
object Routes {
    const val SPLASH = "splash"
    const val WALKTHROUGH = "walkthrough"
    const val ONBOARDING = "onboarding"
    const val AUTH = "auth"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val VOCABULARY = "vocabulary"
    const val VOCABULARY_PRACTICE = "vocabulary_practice"
    const val LEARNING_ROADMAP = "learning_roadmap"
    const val SETTINGS = "settings"
    const val ANALYTICS = "analytics"
    const val ADMIN_CENTER = "admin_center"
    const val USER_MANAGEMENT = "user_management"
    const val CONTENT_CMS = "content_cms"
    const val PLACEMENT_TEST = "placement_test"
    const val SUPPORT_PORTAL = "support_portal"
    const val SUBSCRIPTION = "subscription"
    const val AI_PARTNER = "ai_partner"
    const val PROFILE = "profile"
    
    // Onboarding sub-routes
    const val ONBOARDING_WELCOME = "onboarding_welcome"
    const val ONBOARDING_USER_TYPE = "onboarding_user_type"
    const val ONBOARDING_GOAL_SETTING = "onboarding_goal_setting"
    const val ONBOARDING_TUTORIAL = "onboarding_tutorial"
}

/**
 * Navigation graph for the app
 * TODO: Add actual screen composables when they are created
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SPLASH) {
            com.example.srlappexperiment.presentation.ui.screens.splash.SplashScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.WALKTHROUGH) {
            com.example.srlappexperiment.presentation.ui.screens.onboarding.WalkthroughScreen(
                onFinished = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.LOGIN) {
            com.example.srlappexperiment.presentation.ui.screens.auth.LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            com.example.srlappexperiment.presentation.ui.screens.auth.RegistrationScreen(
                onRegisterSuccess = {
                    // Navigate to Questionnaire
                    navController.navigate(Routes.ONBOARDING)
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.ONBOARDING) {
            com.example.srlappexperiment.presentation.ui.screens.onboarding.QuestionnaireScreen(
                onComplete = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.DASHBOARD) {
            com.example.srlappexperiment.presentation.ui.screens.main.MainScreen(
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToAiPartner = { navController.navigate(Routes.AI_PARTNER) },
                onNavigateToPlacement = { navController.navigate(Routes.PLACEMENT_TEST) },
                onNavigateToSubscription = { navController.navigate(Routes.SUBSCRIPTION) },
                onNavigateToAdmin = { navController.navigate(Routes.ADMIN_CENTER) },
                onNavigateToSupport = { navController.navigate(Routes.SUPPORT_PORTAL) }
            )
        }

        composable(Routes.VOCABULARY) {
            com.example.srlappexperiment.presentation.ui.screens.vocabulary.VocabularyLibraryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.VOCABULARY_PRACTICE) {
            com.example.srlappexperiment.presentation.ui.screens.vocabulary.VocabularyPracticeScreen(
                onSessionComplete = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            com.example.srlappexperiment.presentation.ui.screens.settings.SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToAdmin = { navController.navigate(Routes.ADMIN_CENTER) },
                onNavigateToSupport = { navController.navigate(Routes.SUPPORT_PORTAL) },
                onNavigateToSubscription = { navController.navigate(Routes.SUBSCRIPTION) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.AI_PARTNER) {
            com.example.srlappexperiment.presentation.ui.screens.ai.AIConversationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ANALYTICS) {
            com.example.srlappexperiment.presentation.ui.screens.dashboard.AnalyticsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.ADMIN_CENTER) {
            com.example.srlappexperiment.presentation.ui.screens.admin.AdminCenterScreen(
                onNavigateToUserManagement = { navController.navigate(Routes.USER_MANAGEMENT) },
                onNavigateToCMS = { navController.navigate(Routes.CONTENT_CMS) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.USER_MANAGEMENT) {
            com.example.srlappexperiment.presentation.ui.screens.admin.UserManagementScreen(
                onNavigateToUserDetail = { /* To be implemented */ },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CONTENT_CMS) {
            com.example.srlappexperiment.presentation.ui.screens.admin.ContentCMSScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PLACEMENT_TEST) {
            com.example.srlappexperiment.presentation.ui.screens.placement.PlacementTestScreen(
                onComplete = { level ->
                    // Update user level and navigate to dashboard
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.PLACEMENT_TEST) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SUPPORT_PORTAL) {
            com.example.srlappexperiment.presentation.ui.screens.support.SupportPortalScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SUBSCRIPTION) {
            com.example.srlappexperiment.presentation.ui.screens.billing.SubscriptionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }


    }
}
