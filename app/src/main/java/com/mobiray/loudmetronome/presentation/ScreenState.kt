package com.mobiray.loudmetronome.presentation

sealed class ScreenState {

    data object Loading : ScreenState()

    data object RequestPermission : ScreenState()

    data class Metronome(
        val isPlaying: Boolean,
        val bpm: Int = 120,
        val numerator: Int = 4,
        val denominator: Int = 4,
        val accent: Boolean = true,
        val subbeat: Int = 0
    ) : ScreenState()
}