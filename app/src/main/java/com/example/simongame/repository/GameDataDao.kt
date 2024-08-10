package com.example.simongame.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.simongame.data.GameData
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDataDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(gameData: GameData)

    @Update
    suspend fun update(gameData: GameData)

    @Query("SELECT * from game_data ORDER BY score DESC")
    fun getAllOrderedByScore(): Flow<List<GameData>>

    @Query("SELECT * from game_data ORDER BY timestamp DESC")
    fun getAllOrderedByLatest(): Flow<List<GameData>>
}