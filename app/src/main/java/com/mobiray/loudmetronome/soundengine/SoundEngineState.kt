package com.mobiray.loudmetronome.soundengine

data class SoundEngineState(
    val isPlaying: Boolean,
    val bpm: Int,
    val numerator: Int,
    val denominator: Int,
    val accent: Boolean,
    val subbeat: Int,
)