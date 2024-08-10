package com.example.simongame.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_data")
data class GameData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val score: Int,
    val playerName: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val playerPicture: ByteArray,
    val timestamp: Long
)
