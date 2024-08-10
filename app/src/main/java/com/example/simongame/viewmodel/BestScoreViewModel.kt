package com.example.simongame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simongame.repository.GameDataRepository
import com.example.simongame.viewmodel.uistate.GameDataUiState
import com.example.simongame.viewmodel.uistate.toUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BestScoreViewModel(
    gameDataRepository: GameDataRepository
): ViewModel() {

    // Ui state implementation

    data class UiState(
        val bestScores: List<GameDataUiState> = emptyList()
    )

    val uiState: StateFlow<UiState> = gameDataRepository.getAllOrderedByScore().map {
            gameData -> UiState(bestScores = gameData.map { it.toUiState() })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )
}