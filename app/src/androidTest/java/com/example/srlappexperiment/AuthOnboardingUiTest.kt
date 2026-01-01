package com.example.srlappexperiment

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.srlappexperiment.presentation.ui.screens.auth.RegistrationScreen
import com.example.srlappexperiment.presentation.ui.screens.onboarding.QuestionnaireScreen
import com.example.srlappexperiment.ui.theme.SRLAppExperimentTheme
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests for Auth and Onboarding flows
 * These tests verify the visual state and interaction logic of the screens.
 */
class AuthOnboardingUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registrationScreen_validation_enablesButton() {
        composeTestRule.setContent {
            SRLAppExperimentTheme {
                RegistrationScreen(
                    onRegisterSuccess = {},
                    onNavigateToLogin = {}
                )
            }
        }

        // Initially disabled
        composeTestRule.onNodeWithText("Create Account").assertIsNotEnabled()

        // Fill fields
        composeTestRule.onNodeWithText("Full Name").performTextInput("John Doe")
        composeTestRule.onNodeWithText("Email Address").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("password123")

        // Should be enabled now
        composeTestRule.onNodeWithText("Create Account").assertIsEnabled()
    }

    @Test
    fun questionnaireScreen_navigation_works() {
        composeTestRule.setContent {
            SRLAppExperimentTheme {
                QuestionnaireScreen(
                    onComplete = {}
                )
            }
        }

        // Welcome step
        composeTestRule.onNodeWithText("Welcome to SRL App!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue").performClick()

        // Language selection step
        composeTestRule.onNodeWithText("Which language do you want to learn?").assertIsDisplayed()
        
        // Select Spanish
        composeTestRule.onNodeWithText("Spanish").performClick()
        composeTestRule.onNodeWithText("Continue").performClick()

        // Level selection step
        composeTestRule.onNodeWithText("How much do you know?").assertIsDisplayed()
        
        // Select Beginner
        composeTestRule.onNodeWithText("Beginner").performClick()
        composeTestRule.onNodeWithText("Continue").performClick()

        // Back button should work
        composeTestRule.onNodeWithText("Back").performClick()
        composeTestRule.onNodeWithText("How much do you know?").assertIsDisplayed()
    }
}
