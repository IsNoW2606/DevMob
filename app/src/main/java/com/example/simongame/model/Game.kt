package com.example.simongame.model

class Game {
    fun nextLevel() {
        currentLevel++
        guessRemaining = currentLevel

        val newSequence = currentSequence.copyOf()
        currentSequence = Array(currentLevel) { Light.UNSPECIFIED }
        newSequence.copyInto(currentSequence)
        currentSequence[currentLevel - 1] = getRandomLight()
    }

    fun playGreenLight() = playLight(Light.GREEN)
    fun playRedLight() = playLight(Light.RED)
    fun playYellowLight() = playLight(Light.YELLOW)
    fun playBlueLight() = playLight(Light.BLUE)

    private fun playLight(light: Light) {
        val nextLight = currentSequence[currentLevel - guessRemaining]
        if(nextLight != light) {
            isFinished = true
        }

        guessRemaining--
    }

    private var currentSequence: Array<Light> = Array(1) { getRandomLight() }
    fun getCurrentSequence() = currentSequence

    private var currentLevel: Int = 1
    fun getCurrentLevel(): Int = currentLevel

    private var guessRemaining = 1;
    fun hasGuessRemaining(): Boolean = guessRemaining > 0

    private var isFinished = false
    fun isFinished(): Boolean = isFinished

    private fun getRandomLight(): Light {
        return Light.entries[(Math.random() * 4).toInt()]
    }

    enum class Light {
        GREEN,
        RED,
        YELLOW,
        BLUE,
        UNSPECIFIED
    }
}