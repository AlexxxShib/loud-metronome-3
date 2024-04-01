package com.mobiray.loudmetronome.presentation

import com.mobiray.loudmetronome.soundengine.preset.Segment

sealed class ScreenState {

    data object Loading : ScreenState()

    data class Metronome(
        val isPlaying: Boolean,
        val segment: Segment
    ) : ScreenState()
}