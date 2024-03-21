package com.mobiray.loudmetronome.soundengine

import com.mobiray.loudmetronome.soundengine.preset.Segment

data class State(
    val isPlaying: Boolean,
    val segment: Segment
)