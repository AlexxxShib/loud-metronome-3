package com.mobiray.loudmetronome.soundengine.preset

import com.mobiray.loudmetronome.soundengine.Settings

data class Segment(
    private var bpm: Int = 120,
    private var numerator: Int = 4,
    private var denominator: Int = 4,
    private var accent: Boolean = true,
    private var subbeat: Int = 0,
    var repeatCount: Int = -1
) {

    var bpmValue: Int
        get() = bpm
        set(value) {
            bpm = value.coerceIn(Settings.MIN_BPM, Settings.MAX_BPM)
        }

    val lowLevelBpm: Int
        get() = (bpm * (denominator * 1.0 / Settings.BPM_BASE) * (subbeat + 1)).toInt()

    var numeratorValue: Int
        get() = numerator
        set(value) {
            numerator = value
            updateBeats()
        }

    var denominatorValue: Int
        get() = denominator
        set(value) {
            denominator = value
            updateBeats()
        }

    var accentValue: Boolean
        get() = accent
        set(value) {
            accent = value
            updateBeats()
        }

    var subbeatValue: Int
        get() = subbeat
        set(value) {
            subbeat = value
            updateBeats()
        }

    val beatSize: Int
        get() = numerator + numerator * subbeat

    private var beats: Array<Int> = arrayOf()
    private var curBeatIndex = 0

    val beat: Int
        get() {
            if (curBeatIndex >= beats.size) {
                curBeatIndex = 0
            }
            return try {
                beats[curBeatIndex++]
            } catch (e: ArrayIndexOutOfBoundsException) {
                if (accent) {
                    2
                } else {
                    1
                }
            }
        }

    init {
        bpmValue = bpm

        updateBeats()
    }

    fun addBpm(addBpm: Int) {
        this.bpm += addBpm
    }

    fun updateBeats() {
        beats = Array(beatSize) { 0 }
        curBeatIndex = 0
        for (i in beats.indices) {
            if (i % (subbeat + 1) == 0) {
                beats[i] = 1
            } else {
                beats[i] = 0
            }
        }
        if (accent) {
            beats[0] = 2
        }
    }
}
