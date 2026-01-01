package com.example.srlappexperiment

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.srlappexperiment.presentation.ui.screens.dashboard.DashboardScreen
import com.example.srlappexperiment.presentation.ui.screens.settings.SettingsScreen
import com.example.srlappexperiment.presentation.ui.screens.vocabulary.VocabularyPracticeScreen
import com.example.srlappexperiment.ui.theme.SRLAppExperimentTheme
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests for Core Application Flows
 * These tests verify the Dashboard, Vocabulary Practice, and Settings screens.
 */
class CoreFlowsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboard_displaysStatsAndNavigation() {
        composeTestRule.setContent {
            SRLAppExperimentTheme {
                DashboardScreen(
                    onNavigateToPractice = {},
                    onNavigateToSettings = {},
                    onNavigateToLibrary = {}
                )
            }
        }

        // Verify Dashboard header
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
        
        // Verify Streak card exists
        composeTestRule.onNodeWithText("Practice Now").assertIsDisplayed()
        
        // Verify Quick Action cards
        composeTestRule.onNodeWithText("Vocabulary").assertIsDisplayed()
        composeTestRule.onNodeWithText("Speaking").assertIsDisplayed()
    }

    @Test
    fun practice_cardFlipAndRatingButtons() {
        composeTestRule.setContent {
            SRLAppExperimentTheme {
                VocabularyPracticeScreen(
                    onSessionComplete = {},
                    onNavigateBack = {}
                )
            }
        }

        // Initially, rating buttons should NOT be visible
        composeTestRule.onNodeWithText("Again").assertDoesNotExist()
        composeTestRule.onNodeWithText("Good").assertDoesNotExist()

        // Reveal answer (tap on card area - assuming generic tap works or finding text)
        composeTestRule.onNodeWithText("Tap to reveal answer").performClick()

        // Now rating buttons should be visible
        composeTestRule.onNodeWithText("Again").assertIsDisplayed()
        composeTestRule.onNodeWithText("Good").assertIsDisplayed()
        composeTestRule.onNodeWithText("Easy").assertIsDisplayed()
    }

    @Test
    fun settings_displaysProfileAndToggles() {
        composeTestRule.setContent {
            SRLAppExperimentTheme {
                SettingsScreen(
                    onBack = {},
                    onNavigateToAdmin = {},
                    onNavigateToSupport = {},
                    onNavigateToSubscription = {},
                    onLogout = {}
                )
            }
        }

        // Verify Settings header
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        
        // Verify Dark Mode switch
        composeTestRule.onNodeWithText("Dark Mode").assertIsDisplayed()
        
        // Open Edit Name dialog
        composeTestRule.onNodeWithContentDescription("Edit Name").performClick()
        composeTestRule.onNodeWithText("Edit Display Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").performClick()
    }
}
