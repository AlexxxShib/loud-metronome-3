package com.mobiray.loudmetronome.soundengine

import com.mobiray.loudmetronome.soundengine.preset.Preset
import kotlinx.coroutines.flow.StateFlow

interface ISoundEngine {

    fun getStateFlow(): StateFlow<State>

    fun loadPreset(preset: Preset)

    fun startStopPlayback(isPlay: Boolean)

    fun addBpm(addBpmValue: Int)

    fun changeNumerator(numerator: Int)

    fun changeDenominator(denominator: Int)

    fun changeSubbeat(subbeat: Int)

    fun changeAccent(accent: Boolean)

    fun changeBpm(bpm: Int)
}