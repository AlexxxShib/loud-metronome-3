package com.mobiray.loudmetronome.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.mobiray.loudmetronome.soundengine.SoundEngine
import com.mobiray.loudmetronome.soundengine.preset.Segment

class MetronomeService : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MetronomeService = this@MetronomeService
    }

    private val soundEngine by lazy {
        SoundEngine(this, object : SoundEngine.Callback {
            override fun onModelChangeCallback(isPlaying: Boolean, segment: Segment) {
                Log.d(TAG, "model changed")
            }

        }, 0)
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        startAsForegroundService()

        soundEngine.startStopPlayback(true)

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

    companion object {

        private const val TAG = "MetronomeService_TAG"

        private const val SERVICE_ID = 1
    }
}