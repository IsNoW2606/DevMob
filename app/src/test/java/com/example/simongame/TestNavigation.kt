package com.example.simongame

import android.content.res.Resources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestNavigation {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Every test are starting from the starting screen wish is the HomeScreen
    @Before
    fun setUp() {
        composeTestRule.launchApp()
    }

    @Test
    fun playButtonClick_navigateToGame() {
        val playButton = composeTestRule.onNodeWithText("Play")

        // Button is displayed (we are on the HomeScreen)
        playButton.assertIsDisplayed()

        // After click we are supposed to navigate to GameScreen
        playButton.performClick()
        playButton.assertIsNotDisplayed() // The button isn't displayed anymore

        // The GameScreen is showed for the user to start the game
        composeTestRule.onNodeWithText("Press to start").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation() {
        val bestScoreNavigationButton = composeTestRule.onNodeWithText("Best score")

        // On the HomeScreen the navigation bar is displayed
        bestScoreNavigationButton.assertIsDisplayed()

        // After click we are supposed to navigate to BestScoreScreen
        bestScoreNavigationButton.performClick()

        // The BestScoreScreen is showed with the Best score table and the Best score navigation button
        composeTestRule.onAllNodesWithText("Best score").assertCountEquals(2)
    }

    
}