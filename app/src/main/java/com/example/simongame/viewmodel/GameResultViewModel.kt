package com.example.simongame.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simongame.data.GameData
import com.example.simongame.repository.GameDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class GameResultViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameDataRepository: GameDataRepository,
    private val applicationContext: Context
): ViewModel() {

    // Ui state implementation

    data class UiState(
        val playerName: String = "",
        val playerPicture: Bitmap? = null,
        val saveButtonEnable: Boolean = false,
        val score: Int = 0,
        val hasSavedGame: Boolean = false,

        // Event
        val requestingCameraPermission: Boolean = false,
        val openingCamera: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState()
    )

    private fun updateUiState(
        playerName: String = _uiState.value.playerName,
        playerPicture: Bitmap? = _uiState.value.playerPicture,
        saveButtonEnable: Boolean = _uiState.value.saveButtonEnable,
        score: Int = _uiState.value.score,
        hasSavedGame: Boolean = _uiState.value.hasSavedGame,
        requestingCameraPermission: Boolean = _uiState.value.requestingCameraPermission,
        openingCamera: Boolean = _uiState.value.openingCamera
    ) {
        _uiState.update {
            UiState(
                playerName = playerName,
                playerPicture = playerPicture,
                saveButtonEnable = saveButtonEnable,
                score = score,
                hasSavedGame = hasSavedGame,
                requestingCameraPermission = requestingCameraPermission,
                openingCamera = openingCamera
            )
        }
    }

    init {
        // We recover the score from the navigation argument
        val score: Int = savedStateHandle["score"]!!
        updateUiState(score = score)
    }

    fun onPlayerNameValueChanged(playerName: String) = updateUiState(playerName = playerName, saveButtonEnable = playerName.isNotBlank())

    fun onSaveButtonClicked() {
        val playerPicture = _uiState.value.playerPicture
        val stream = ByteArrayOutputStream();
        playerPicture?.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        val gameData = GameData(
            score = _uiState.value.score,
            playerName = _uiState.value.playerName,
            playerPicture = stream.toByteArray(),
            timestamp = System.currentTimeMillis()
        )

        // Save game data to local Db
        viewModelScope.launch {
            gameDataRepository.insert(gameData)
        }

        playerPicture?.recycle()

        updateUiState(hasSavedGame = true)
    }

    fun onTakePictureResult(picture: Bitmap?) {
        if (picture == null)
            return

        updateUiState(playerPicture = picture)
    }

    fun onPictureDelete() = updateUiState(playerPicture = null)

    fun onOpeningCamera(permission: Boolean = true) {
        if (!permission)
            return

        val permissionCheckResult = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            updateUiState(openingCamera = true)
        } else {
            updateUiState(requestingCameraPermission = true)
        }
    }

    fun onRequestPermissionResult(permission: Boolean) = updateUiState(openingCamera = permission, requestingCameraPermission = false)

    fun onCameraOpened() = updateUiState(openingCamera = false)
}