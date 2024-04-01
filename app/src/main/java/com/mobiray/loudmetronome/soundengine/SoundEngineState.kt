package com.mobiray.loudmetronome.soundengine

import com.mobiray.loudmetronome.soundengine.preset.Segment

data class SoundEngineState(
    val isPlaying: Boolean,
    val segment: Segment
)