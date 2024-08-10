package com.example.simongame.util

import android.content.Context
import android.content.res.AssetManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import com.example.simongame.R
import java.io.FileDescriptor

class SimonSoundBoard(context: Context): Thread() {
    private var soundPlayer: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
    ).build()

    private val _soundFileId: Array<Int> = Array(5) { 0 }

    init {
        _soundFileId[0] = soundPlayer.load(context, R.raw.green_sound, 1)
        _soundFileId[1] = soundPlayer.load(context, R.raw.red_sound, 1)
        _soundFileId[2] = soundPlayer.load(context, R.raw.yellow_sound, 1)
        _soundFileId[3] = soundPlayer.load(context, R.raw.blue_sound, 1)
        _soundFileId[4] = soundPlayer.load(context, R.raw.error_sound, 1)
    }

    fun playGreenLightSound() {
        playSound(0)
    }

    fun playRedLightSound() {
        playSound(1)
    }

    fun playYellowLightSound() {
        playSound(2)
    }

    fun playBlueLightSound() {
        playSound(3)
    }

    fun playErrorSound() {
        playSound(4)
    }

    fun cancelLastSound() {
        soundPlayer.stop(lastStreamId)
    }

    private var lastStreamId: Int = 0
    private fun playSound(index: Int) {
        lastStreamId = soundPlayer.play(_soundFileId[index], 1f, 1f, 0,0, 1f)
    }
}