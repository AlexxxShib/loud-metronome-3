package com.mobiray.loudmetronome.soundengine

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import com.mobiray.loudmetronome.soundengine.preset.Preset
import com.mobiray.loudmetronome.soundengine.preset.Segment
import com.mobiray.loudmetronome.soundengine.sample.Sample
import com.mobiray.loudmetronome.soundengine.sample.SampleLoader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Arrays
import kotlin.concurrent.thread


class SoundEngine(
    private val context: Context,
    samplePackIndex: Int
) : ISoundEngine {

    private val stateFlow: MutableStateFlow<State>

    private var playbackThread: Thread? = null

    private var sampleLow: ByteArray = ByteArray(0)
    private var sampleMid: ByteArray = ByteArray(0)
    private var sampleHi: ByteArray = ByteArray(0)

    private var samples: Array<ByteArray?> = arrayOf()

    private var audioTrack: AudioTrack? = null
    private var minBufferSize = 0

    var currentPreset: Preset?
        private set

    var isPlaying = false
        private set

    init {
        Log.d(TAG, "SoundEngine created")

        val preset = Preset(Segment()) // 120, 4/4 - defaults
        currentPreset = preset

        val sample = SampleLoader.getSampleList(context)[samplePackIndex]
        setSamplePack(sample)

        stateFlow = MutableStateFlow(State(isPlaying, preset.getSegment()))

        modelChangeCallback()
    }

    private fun setSamplePack(sample: Sample) {
        minBufferSize = AudioTrack.getMinBufferSize(
            Settings.FREQ,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        sampleLow = SampleLoader.load(context, sample.samplePaths[0])
        sampleMid = SampleLoader.load(context, sample.samplePaths[1])
        sampleHi = SampleLoader.load(context, sample.samplePaths[2])

        samples = arrayOf(
            Arrays.copyOfRange(sampleLow, 44, sampleLow.size),
            Arrays.copyOfRange(sampleMid, 44, sampleMid.size),
            Arrays.copyOfRange(sampleHi, 44, sampleHi.size),
        )
    }

    private fun stopPlayback() {
        Log.d(TAG, "stopPlayback")

        isPlaying = false

        val preset = currentPreset ?: throw NullPointerException("Current preset is NULL")

        for (i in preset.segmentList.indices) {
            preset.segmentList[i].updateBeats()
        }

        preset.getSegment().updateBeats() // todo: ???

        try {
            audioTrack?.stop()
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        try {
            audioTrack?.release()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun startPlayback(preset: Preset) {
        stopPlayback()

        val prevPlaybackThread = playbackThread
        if (prevPlaybackThread != null) {
            try {
                prevPlaybackThread.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        Log.d(TAG, "startPlayback")

        isPlaying = true
        val currentAudioTrack = AudioTrack( // todo: deprecated
            AudioManager.STREAM_MUSIC,
            Settings.FREQ,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize,
            AudioTrack.MODE_STREAM
        )

        audioTrack = currentAudioTrack
        currentAudioTrack.play()

        playbackThread = thread {
            Log.d(TAG_THREAD, "run()")

            for (i in currentPreset!!.segmentList.indices) { // todo: use preset
                currentPreset!!.segmentList[i].updateBeats()
            }

            for (segmentIndex in 0 until preset.segmentList.size) {
                val segment = preset.getSegment(segmentIndex)
                if (!isPlaying) {
                    Log.d(TAG_THREAD, "run.end() 1")
                    return@thread
                }

                var j = 0
                while (j < segment.repeatCount * (segment.beatSize) || segment.repeatCount <= 0) {
                    if (!isPlaying) {
                        Log.d(TAG_THREAD, "run.end() 2")
                        return@thread
                    }

                    val intervalSize = 2 * (Settings.FREQ * (60.0 / segment.lowLevelBpm)).toInt()
                    val currentSample = when (segment.beat) {
                        0 -> {
                            Log.d(TAG_THREAD, "sampleLow")
                            sampleLow
                        }

                        1 -> {
                            Log.d(TAG_THREAD, "sampleMid")
                            sampleMid
                        }

                        2 -> {
                            Log.d(TAG_THREAD, "sampleHi")
                            sampleHi
                        }

                        else -> throw IllegalStateException("Unknown sample ID: ${segment.beat}")
                    }

                    try {
                        currentAudioTrack.write(currentSample.copyOf(intervalSize), 0, intervalSize)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }

                    j++
                }
            }
            Log.d(TAG_THREAD, "run.end() 3")
        }
    }

    private fun modelChangeCallback(segmentId: Int = 0) {
        val currentSegment = currentPreset?.getSegment(segmentId)?.copy()
            ?: throw NullPointerException("Current segment $segmentId is NULL")

        stateFlow.value = State(isPlaying, currentSegment)
    }

    override fun getStateFlow() = stateFlow

    override fun loadPreset(preset: Preset) {
        currentPreset = preset
        modelChangeCallback()
    }

    override fun startStopPlayback(isPlay: Boolean) {
        if (isPlay) {
            val preset = currentPreset ?: throw IllegalStateException("Preset is not set")
            startPlayback(preset)
        } else {
            stopPlayback()
        }
        modelChangeCallback()
    }

    override fun addBpm(addBpmValue: Int) {
        currentPreset?.getSegment()?.addBpm(addBpmValue)
        modelChangeCallback()
    }

    override fun changeNumerator(numerator: Int) {
        currentPreset?.getSegment()?.numeratorValue = numerator
        modelChangeCallback()
    }

    override fun changeDenominator(denominator: Int) {
        currentPreset?.getSegment()?.denominatorValue = denominator
        modelChangeCallback()
    }

    override fun changeSubbeat(subbeat: Int) {
        currentPreset?.getSegment()?.subbeatValue = subbeat
        modelChangeCallback()
    }

    override fun changeAccent(accent: Boolean) {
        currentPreset?.getSegment()?.accentValue = accent
        modelChangeCallback()
    }

    override fun changeBpm(bpm: Int) {
        currentPreset?.getSegment()?.bpmValue = bpm
        modelChangeCallback()
    }

    companion object {

        private const val TAG = "SoundEngine_TAG"

        private const val TAG_THREAD = "SoundEngine_PlaybackThread_TAG"
    }
}

