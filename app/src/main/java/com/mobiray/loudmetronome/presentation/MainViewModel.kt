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
import androidx.lifecycle.viewModelScope
import com.mobiray.loudmetronome.service.MetronomeService
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    private var metronomeService: MetronomeService? = null
    private var serviceBoundState by mutableStateOf(false)

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected")

            val binder = service as MetronomeService.LocalBinder
            metronomeService = binder.getService()
            serviceBoundState = true

            onServiceConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")

            serviceBoundState = false
            metronomeService = null

            onServiceDisconnected()
        }
    }

    private var collectSoundEngineStateJob: Job? = null

    private val _screenStateFlow = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenStateFlow: StateFlow<ScreenState>
        get() = _screenStateFlow

    private val isPlaying
        get() = (_screenStateFlow.value as? ScreenState.Metronome)?.isPlaying ?: false

    fun tryStartMetronomeService() {
        startForegroundService()
    }

    fun tryStopMetronomeService() {
        if (!isPlaying) {
            metronomeService?.stopForegroundService()
        }
    }

    fun playStop() {
        metronomeService?.startStopPlayback(!isPlaying)
    }

    fun changeBpm(changeValue: Int) {
        metronomeService?.addBpm(changeValue)
    }

    private fun onServiceConnected() {
        collectSoundEngineStateJob = viewModelScope.launch {
            metronomeService?.getStateFlow()?.collect {
                with(it) {
                    _screenStateFlow.emit(
                        ScreenState.Metronome(
                            isPlaying = isPlaying,
                            bpm = bpm,
                            numerator = numerator,
                            denominator = denominator,
                            accent = accent,
                            subbeat = subbeat
                        )
                    )
                }
            }
        }
    }

    private fun onServiceDisconnected() {
        collectSoundEngineStateJob?.cancel()
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