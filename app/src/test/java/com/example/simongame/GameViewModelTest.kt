package com.example.simongame

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.simongame.model.Game
import com.example.simongame.util.SimonSoundBoard
import com.example.simongame.viewmodel.GameViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class GameViewModelTest {
    private val viewModel = GameViewModel(
        Game(),
        SimonSoundBoard(ApplicationProvider.getApplicationContext())
    )

    @Test
    fun gameViewModel_OnStartingGame_GameStart() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.onStartGameClicked()

        // When onStartGameClicked is called the state should get the shown value change
        val uiState = viewModel.uiState.value
        assert(uiState.level == 1)
        assert(uiState.isGameStarted)
        assert(uiState.isShowingSequence)

        collectJob.cancel()
    }


    @Test
    fun gameViewModel_OnGreenLightClicked_UiStateUpdated() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.onGreenLightClicked()

        var uiState = viewModel.uiState.value
        assert(uiState.isLaunchingGreenLightAnimation) // is True

        // This method is called after launching the animation
        viewModel.onGreenLightAnimationLaunched()

        uiState = viewModel.uiState.value
        assert(!uiState.isLaunchingGreenLightAnimation) // is False

        collectJob.cancel()
    }

    @Test
    fun gameViewModel_OnRedLightClicked_UiStateUpdated() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.onRedLightClicked()

        var uiState = viewModel.uiState.value
        assert(uiState.isLaunchingRedLightAnimation) // is True

        // This method is called after launching the animation
        viewModel.onRedLightAnimationLaunched()

        uiState = viewModel.uiState.value
        assert(!uiState.isLaunchingRedLightAnimation) // is False

        collectJob.cancel()
    }

    @Test
    fun gameViewModel_OnYellowLightClicked_UiStateUpdated() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.onYellowLightClicked()

        var uiState = viewModel.uiState.value
        assert(uiState.isLaunchingYellowLightAnimation) // is True

        // This method is called after launching the animation
        viewModel.onYellowLightAnimationLaunched()

        uiState = viewModel.uiState.value
        assert(!uiState.isLaunchingYellowLightAnimation) // is False

        collectJob.cancel()
    }

    @Test
    fun gameViewModel_OnBlueLightClicked_UiStateUpdated() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.onBlueLightClicked()

        var uiState = viewModel.uiState.value
        assert(uiState.isLaunchingBlueLightAnimation) // is True

        // This method is called after launching the animation
        viewModel.onBlueLightAnimationLaunched()

        uiState = viewModel.uiState.value
        assert(!uiState.isLaunchingBlueLightAnimation) // is False

        collectJob.cancel()
    }
}