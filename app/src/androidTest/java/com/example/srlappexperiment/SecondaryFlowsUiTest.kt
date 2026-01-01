package com.example.srlappexperiment

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.srlappexperiment.presentation.ui.screens.auth.LoginScreen
import com.example.srlappexperiment.presentation.ui.screens.onboarding.WalkthroughScreen
import com.example.srlappexperiment.presentation.ui.screens.vocabulary.VocabularyLibraryScreen
import com.example.srlappexperiment.ui.theme.SRLAppExperimentTheme
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests for Secondary Application Flows
 * These tests verify the Login, Walkthrough, and Vocabulary Library screens.
 */
class SecondaryFlowsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_validation_enablesSignIn() {
        composeTestRule.setContent {
            SRLAppExperimentTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }

        // Initially disabled if empty
        composeTestRule.onNodeWithText("Sign In").assertIsNotEnabled()

        // Fill credentials
        composeTestRule.onNodeWithText("Email").performTextInput("user@test.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Should be enabled now
        composeTestRule.onNodeWithText("Sign In").assertIsEnabled()
    }

    @Test
    fun walkthrough_navigation_continuesToNextSlides() {
        composeTestRule.setContent {
            SRLAppExperimentTheme {
                WalkthroughScreen(
                    onFinished = {},
                    onNavigateToLogin = {}
                )
            }
        }

        // Check first slide
        composeTestRule.onNodeWithText("Master Any Language, Your Way").assertIsDisplayed()
        
        // Click Continue
        composeTestRule.onNodeWithText("Continue").performClick()
        
        // Check second slide
        composeTestRule.onNodeWithText("Learn Smarter, Not Harder").assertIsDisplayed()
        
        // Click Continue again
        composeTestRule.onNodeWithText("Continue").performClick()
        
        // Check third slide
        composeTestRule.onNodeWithText("Practice Real Conversations").assertIsDisplayed()
    }

    @Test
    fun vocabularyLibrary_displaysSearchAndFilter() {
        composeTestRule.setContent {
            SRLAppExperimentTheme {
                VocabularyLibraryScreen(
                    onNavigateBack = {}
                )
            }
        }

        // Verify title
        composeTestRule.onNodeWithText("Vocabulary Library").assertIsDisplayed()
        
        // Verify search field exists
        composeTestRule.onNodeWithText("Search words...").assertIsDisplayed()
        
        // Verify filter chips exist
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
        composeTestRule.onNodeWithText("Newbie").assertIsDisplayed()
        composeTestRule.onNodeWithText("Learner").assertIsDisplayed()
    }
}
