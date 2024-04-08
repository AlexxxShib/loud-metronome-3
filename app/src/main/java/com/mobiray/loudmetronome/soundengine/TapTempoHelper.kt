package com.mobiray.loudmetronome.soundengine

import java.util.Arrays

class TapTempoHelper {

    private val taps: LongArray = LongArray(Settings.MAX_TAPS) { 0 }

    fun tapTempo(bpmCallback: (Int) -> Unit) {
        val touchTime = System.currentTimeMillis()
        for (i in taps.indices.reversed()) {
            if (taps[i] != 0L && touchTime - taps[i] > 60000 / Settings.MIN_BPM) {
                Arrays.fill(taps, 0)
                taps[0] = touchTime
                return
            }
        }
        for (i in taps.indices) {
            if (taps[i] == 0L) {
                taps[i] = touchTime
                break
            }
            if (i == taps.size - 1) {
                for (j in 1 until taps.size) {
                    taps[j - 1] = taps[j]
                }
                taps[i] = touchTime
            }
        }
        var nNotNull = 0
        for (t in taps) {
            if (t > 0) {
                nNotNull++
            }
        }
        if (nNotNull >= Settings.MIN_TAPS) {
            var avg: Long = 0
            for (i in 0 until nNotNull - 1) {
                avg += ((taps[i + 1] - taps[i]) * 1.0 / (nNotNull - 1)).toLong()
            }
            val bpm = (60000.0 / avg).toInt()

            bpmCallback(bpm)
        }
    }
}