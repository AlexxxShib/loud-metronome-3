package com.mobiray.loudmetronome.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.mobiray.loudmetronome.presentation.MetronomeApplication
import com.mobiray.loudmetronome.soundengine.SoundEngine
import com.mobiray.loudmetronome.soundengine.SoundEngineState
import com.mobiray.loudmetronome.soundengine.preset.Preset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MetronomeService : Service(), SoundEngine {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MetronomeService = this@MetronomeService
    }

    private val component by lazy {
        (application as MetronomeApplication).component
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val soundEngine: SoundEngine by lazy {
        component.getSoundEngine()
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()

        coroutineScope.launch {
            soundEngine.getStateFlow().collect {
                Log.d(TAG, "sound engine state: $it")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        startAsForegroundService()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()

        soundEngine.startStopPlayback(false)

        coroutineScope.cancel()
    }

    private fun startAsForegroundService() {
        NotificationsHelper.createNotificationChannel(this)

        val foregroundServiceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }

        ServiceCompat.startForeground(
            this,
            SERVICE_ID,
            NotificationsHelper.buildNotification(this),
            foregroundServiceType
        )
    }

    fun stopForegroundService() {
        stopSelf()
    }

    override fun getStateFlow(): StateFlow<SoundEngineState> {
        return soundEngine.getStateFlow()
    }

    override fun loadPreset(preset: Preset) {
        TODO("Not yet implemented")
    }

    override fun startStopPlayback(isPlay: Boolean) {
        soundEngine.startStopPlayback(isPlay)
    }

    override fun addBpm(addBpmValue: Int) {
        soundEngine.addBpm(addBpmValue)
    }

    override fun changeNumerator(numerator: Int) {
        soundEngine.changeNumerator(numerator)
    }

    override fun changeDenominator(denominator: Int) {
        soundEngine.changeDenominator(denominator)
    }

    override fun changeSubbeat(subbeat: Int) {
        soundEngine.changeSubbeat(subbeat)
    }

    override fun changeAccent(accent: Boolean) {
        soundEngine.changeAccent(accent)
    }

    override fun changeBpm(bpm: Int) {
        soundEngine.changeBpm(bpm)
    }

    companion object {

        private const val TAG = "MetronomeService_TAG"

        private const val SERVICE_ID = 1
    }
}