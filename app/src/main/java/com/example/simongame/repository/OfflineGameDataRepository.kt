package com.example.simongame.repository

import com.example.simongame.data.GameData
import kotlinx.coroutines.flow.Flow

class OfflineGameDataRepository(private val dao: GameDataDao): GameDataRepository {
    override suspend fun insert(gameData: GameData) = dao.insert(gameData)

    override suspend fun update(gameData: GameData) = dao.update(gameData)

    override fun getAllOrderedByScore(): Flow<List<GameData>> = dao.getAllOrderedByScore()

    override fun getAllOrderedByLatest(): Flow<List<GameData>> = dao.getAllOrderedByLatest()
}