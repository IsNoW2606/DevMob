package com.example.simongame

import android.app.Application
import com.example.simongame.repository.AppContainer
import com.example.simongame.repository.AppDataContainer

class MyApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}