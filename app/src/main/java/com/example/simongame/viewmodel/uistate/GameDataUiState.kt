package com.example.simongame.viewmodel.uistate

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.simongame.data.GameData

data class GameDataUiState(
    val score: Int,
    val playerName: String,
    val playerPicture: Bitmap?
)

fun GameData.toUiState() = GameDataUiState(
    score = this.score,
    playerName = this.playerName,
    playerPicture = BitmapFactory.decodeByteArray(this.playerPicture, 0, this.playerPicture.size)
)
