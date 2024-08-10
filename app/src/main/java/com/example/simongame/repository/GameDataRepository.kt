package com.example.simongame.repository

import com.example.simongame.data.GameData
import kotlinx.coroutines.flow.Flow

interface GameDataRepository {
    suspend fun insert(gameData: GameData)

    suspend fun update(gameData: GameData)

    fun getAllOrderedByScore(): Flow<List<GameData>>

    fun getAllOrderedByLatest(): Flow<List<GameData>>
}