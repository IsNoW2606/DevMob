package com.example.simongame

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.simongame.model.Game
import com.example.simongame.util.SimonSoundBoard
import com.example.simongame.view.GameView
import com.example.simongame.viewmodel.BestScoreViewModel
import com.example.simongame.viewmodel.GameResultViewModel
import com.example.simongame.viewmodel.GameViewModel
import com.example.simongame.viewmodel.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            GameViewModel(
                Game(),
                SimonSoundBoard(
                    myApplication().applicationContext
                )
            )
        }
        initializer {
            HomeViewModel(
                myApplication().container.gameDataRepository
            )
        }
        initializer {
            GameResultViewModel(
                this.createSavedStateHandle(),
                myApplication().container.gameDataRepository,
                myApplication().applicationContext
            )
        }

        initializer {
            BestScoreViewModel(
                myApplication().container.gameDataRepository
            )
        }
    }
}

fun CreationExtras.myApplication(): MyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)