package com.example.simongame

import androidx.compose.ui.test.junit4.ComposeContentTestRule

fun ComposeContentTestRule.launchApp() {
    setContent {
        App()
    }
}