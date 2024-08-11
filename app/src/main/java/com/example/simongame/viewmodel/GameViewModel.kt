package com.example.simongame.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simongame.model.Game
import com.example.simongame.util.SimonSoundBoard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private var game: Game = Game(),
    private val soundBoard: SimonSoundBoard
) : ViewModel() {

    private fun showCurrentLevelSequence() {
        viewModelScope.launch {
            updateUiState(isShowingSequence = true) // Telling the UI that we are going to show the sequence

            val currentSequence = game.getCurrentSequence()

            val beforeSequenceDelay = 1000L
            val delayBetweenLightInMillis = 500L

            delay(beforeSequenceDelay) // Waiting for user to be ready before showing the sequence

            for (light in currentSequence) {
                when(light) {
                    Game.Light.GREEN -> playGreenLight()
                    Game.Light.RED -> playRedLight()
                    Game.Light.YELLOW -> playYellowLight()
                    Game.Light.BLUE -> playBlueLight()
                    Game.Light.UNSPECIFIED -> Log.e("SimonGame", "Unspecified light found in the sequence")
                }

                delay(delayBetweenLightInMillis) // Waiting before playing the next light
            }

            updateUiState(isShowingSequence = false) // Telling the UI that we are done showing the sequence
        }
    }

    private fun playGreenLight() {
        soundBoard.playGreenLightSound()
        updateUiState(isLaunchingGreenLightAnimation = true)
    }

    private fun playRedLight() {
        soundBoard.playRedLightSound()
        updateUiState(isLaunchingRedLightAnimation = true)
    }

    private fun playYellowLight() {
        soundBoard.playYellowLightSound()
        updateUiState(isLaunchingYellowLightAnimation = true)
    }

    private fun playBlueLight() {
        soundBoard.playBlueLightSound()
        updateUiState(isLaunchingBlueLightAnimation = true)
    }

    private fun checkGuess() {
        if (game.isFinished()) {
            playError()
        } else if (!game.hasGuessRemaining()) {
            game.nextLevel()
            updateUiState(level = game.getCurrentLevel())
            showCurrentLevelSequence()
        }
    }

    private fun playError() {
        soundBoard.cancelLastSound()
        soundBoard.playErrorSound()
        updateUiState(
            isLaunchingGreenLightAnimation = true,
            isLaunchingRedLightAnimation = true,
            isLaunchingYellowLightAnimation = true,
            isLaunchingBlueLightAnimation = true,
            isGameFinished = true,
        )
    }

    // User event function

    fun onStartGameClicked() {
        updateUiState(isGameStarted = true)
        showCurrentLevelSequence()
    }

    // On light clicked
    fun onGreenLightClicked() {
        playGreenLight()
        game.playGreenLight()
        checkGuess()
    }

    fun onRedLightClicked() {
        playRedLight()
        game.playRedLight()
        checkGuess()
    }

    fun onYellowLightClicked() {
        playYellowLight()
        game.playYellowLight()
        checkGuess()
    }
    fun onBlueLightClicked() {
        playBlueLight()
        game.playBlueLight()
        checkGuess()
    }

    // On light animation launched
    fun onGreenLightAnimationLaunched() = updateUiState(isLaunchingGreenLightAnimation = false)
    fun onRedLightAnimationLaunched() = updateUiState(isLaunchingRedLightAnimation = false)
    fun onYellowLightAnimationLaunched() = updateUiState(isLaunchingYellowLightAnimation = false)
    fun onBlueLightAnimationLaunched() = updateUiState(isLaunchingBlueLightAnimation = false)

    // Ui state implementation

    data class UiState(
        val level: Int = 1,

        val isShowingSequence: Boolean = false, // Using to disable user input when showing the sequence
        val isGameStarted: Boolean = false,
        val isGameFinished: Boolean = false,

        // Animation state
        val isLaunchingGreenLightAnimation: Boolean = false,
        val isLaunchingRedLightAnimation: Boolean = false,
        val isLaunchingYellowLightAnimation: Boolean = false,
        val isLaunchingBlueLightAnimation: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    private fun updateUiState(
        level: Int = _uiState.value.level,
        isShowingSequence: Boolean = _uiState.value.isShowingSequence,
        isGameStarted: Boolean = _uiState.value.isGameStarted,
        isGameFinished: Boolean = _uiState.value.isGameFinished,
        isLaunchingGreenLightAnimation: Boolean = _uiState.value.isLaunchingGreenLightAnimation,
        isLaunchingRedLightAnimation: Boolean = _uiState.value.isLaunchingRedLightAnimation,
        isLaunchingYellowLightAnimation: Boolean = _uiState.value.isLaunchingYellowLightAnimation,
        isLaunchingBlueLightAnimation: Boolean = _uiState.value.isLaunchingBlueLightAnimation,
    ) {
        _uiState.update {
            UiState(
                level = level,
                isShowingSequence = isShowingSequence,
                isGameStarted = isGameStarted,
                isGameFinished = isGameFinished,
                isLaunchingGreenLightAnimation = isLaunchingGreenLightAnimation,
                isLaunchingRedLightAnimation = isLaunchingRedLightAnimation,
                isLaunchingYellowLightAnimation = isLaunchingYellowLightAnimation,
                isLaunchingBlueLightAnimation = isLaunchingBlueLightAnimation,
            )
        }
    }
}