package com.mobiray.loudmetronome.presentation

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.mobiray.loudmetronome.service.MetronomeService

class MetronomeViewModel(private val application: Application): AndroidViewModel(application) {

    private var metronomeService: MetronomeService? = null
    private var serviceBoundState by mutableStateOf(false)

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected")

            val binder = service as MetronomeService.LocalBinder
            metronomeService = binder.getService()
            serviceBoundState = true

//            onServiceConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")

            serviceBoundState = false
            metronomeService = null
        }
    }

    init {
        tryToBindToServiceIfRunning()
    }

    fun play() {
        startForegroundService()
    }

    fun stop() {
        metronomeService?.stopForegroundService()
    }

    private fun startForegroundService() {
        application.startForegroundService(Intent(application, MetronomeService::class.java))

        tryToBindToServiceIfRunning()
    }

    private fun tryToBindToServiceIfRunning() {
        Intent(application, MetronomeService::class.java).also { intent ->
            application.bindService(intent, connection, 0)
        }
    }

    override fun onCleared() {
        super.onCleared()
        application.unbindService(connection)
        metronomeService = null

        Log.d(TAG, "onCleared")
    }

    companion object {

        private const val TAG = "MainViewModel_TAG"
    }
}