package com.mobiray.loudmetronome.presentation

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiray.loudmetronome.service.MetronomeService
import com.mobiray.loudmetronome.soundengine.TapTempoHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val context: Context,
    private val tapTempoHelper: TapTempoHelper
) : ViewModel() {

    private var metronomeService: MetronomeService? = null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected")

            val binder = service as MetronomeService.LocalBinder
            metronomeService = binder.getService()

            onServiceConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")

            metronomeService = null

            onServiceDisconnected()
        }
    }

    private var collectSoundEngineStateJob: Job? = null

    private val _requestNotificationPermissionFlow = MutableSharedFlow<Unit>()
    val requestNotificationPermissionFlow: Flow<Unit>
        get() = _requestNotificationPermissionFlow

    private val _screenStateFlow = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenStateFlow: StateFlow<ScreenState>
        get() = _screenStateFlow

    private val isPlaying: Boolean
        get() {
            val service = metronomeService ?: return false
            return service.getStateFlow().value.isPlaying
        }

    init {
        if (checkNotificationPermissionRequired()) {
            viewModelScope.launch {
                _screenStateFlow.emit(ScreenState.RequestPermission)
            }
        }
    }

    fun tryStartMetronomeService() {
        if (checkNotificationPermissionRequired()) {
            return
        }

        viewModelScope.launch {
            if (metronomeService == null)
            {
                _screenStateFlow.emit(ScreenState.Loading)
                startForegroundService()
            }
        }
    }

    fun tryStopMetronomeService() {
        if (!isPlaying) {
            metronomeService?.stopForegroundService()
        }
    }

    fun requestNotificationPermission() {
        viewModelScope.launch {
            _requestNotificationPermissionFlow.emit(Unit)
        }
    }

    fun handlePermissionRequest() {
        tryStartMetronomeService()
    }

    private fun checkNotificationPermissionRequired(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val permissionStatus = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            )

            return permissionStatus != PackageManager.PERMISSION_GRANTED
        }

        return false
    }

    fun playStop() {
        metronomeService?.startStopPlayback(!isPlaying)
    }

    fun addBpm(changeValue: Int) {
        metronomeService?.addBpm(changeValue)
    }

    fun changeNumerator(numerator: Int) {
        metronomeService?.changeNumerator(numerator)
    }

    fun changeDenominator(denominator: Int) {
        metronomeService?.changeDenominator(denominator)
    }

    fun changeAccent() {
        val currentAccent = (_screenStateFlow.value as? ScreenState.Metronome)?.accent ?: false
        metronomeService?.changeAccent(!currentAccent)
    }

    fun changeSubbeat(subbeatIndex: Int) {
        metronomeService?.changeSubbeat(subbeatIndex)
    }

    fun tapTempo() {
        tapTempoHelper.tapTempo {
            metronomeService?.changeBpm(it)
        }
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
        context.startForegroundService(Intent(context, MetronomeService::class.java))

        tryToBindToServiceIfRunning()
    }

    private fun tryToBindToServiceIfRunning() {
        Intent(context, MetronomeService::class.java).also { intent ->
            context.bindService(intent, connection, 0)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (metronomeService != null) {
            context.unbindService(connection)
            metronomeService = null
        }
        Log.d(TAG, "onCleared")
    }

    companion object {

        private const val TAG = "MainViewModel_TAG"
    }
}