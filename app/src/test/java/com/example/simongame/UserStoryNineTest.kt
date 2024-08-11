package com.example.simongame

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performKeyPress
import androidx.compose.ui.test.requestFocus
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserStoryNineTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Every test are starting from the starting screen wish is the HomeScreen
    @Before
    fun setUp() {
        composeTestRule.launchApp()
    }

    @Test
    fun userStoryNine_onBackPressed_leaveGameWithoutSavingIt() {
        // Navigate to game
        val playButton = composeTestRule.onNodeWithText("Play")
        playButton.performClick()

        // Launching a game
        val startGame = composeTestRule.onNodeWithText("Press to start")
        startGame.performClick()

        // Back pressed
        Espresso.pressBack()

        val home = composeTestRule.onNodeWithText("Home")
        home.assertIsDisplayed() // We are in the HomeScreen

        val lastScore = composeTestRule.onNodeWithText("Last score")
        lastScore.isNotDisplayed() // The game hasn't been saved so the last score table is not displayed
    }
}