package com.example.simongame.repository

import android.content.Context

interface AppContainer {
    val gameDataRepository: GameDataRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val gameDataRepository: GameDataRepository by lazy {
        OfflineGameDataRepository(AppDatabase.getDatabase(context).gameDataDao())
    }
}