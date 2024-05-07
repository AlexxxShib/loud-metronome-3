package com.mobiray.loudmetronome.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiray.loudmetronome.tools.checkNotificationPermissionRequired
import com.mobiray.loudmetronome.service.MetronomeService
import com.mobiray.loudmetronome.soundengine.SoundEngineState
import com.mobiray.loudmetronome.soundengine.TapTempoHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    context: Context,
    private val tapTempoHelper: TapTempoHelper
) : ViewModel() {

    private val metronomeServiceConnectionState = MutableStateFlow<MetronomeService?>(null)

    private val metronomeService: MetronomeService?
        get() = metronomeServiceConnectionState.value

    private val isMetronomeServiceConnected: Boolean
        get() = metronomeServiceConnectionState.value != null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected")

            val binder = service as MetronomeService.LocalBinder
            metronomeServiceConnectionState.value = binder.getService()

            onServiceConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")

            metronomeServiceConnectionState.value = null

            onServiceDisconnected()
        }
    }

    private var collectSoundEngineStateJob: Job? = null

    private val _requestNotificationPermissionFlow = MutableSharedFlow<Unit>()
    val requestNotificationPermissionFlow: Flow<Unit>
        get() = _requestNotificationPermissionFlow

    private val screenFlow = MutableSharedFlow<ScreenState>(1)

    val screenStateFlow: StateFlow<ScreenState> = merge(
        metronomeServiceConnectionState
            .filter { it == null }
            .map { ScreenState.Loading },
        screenFlow
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ScreenState.Loading
    )

    private val isPlaying: Boolean
        get() = metronomeService?.getStateFlow()?.value?.isPlaying ?: false

    private val isAccent: Boolean
        get() = metronomeService?.getStateFlow()?.value?.accent ?: false

    init {
        if (checkNotificationPermissionRequired(context)) {
            viewModelScope.launch {
                screenFlow.emit(ScreenState.RequestPermission)
            }
        }
    }

    fun tryStartMetronome(context: Context) {
        if (checkNotificationPermissionRequired(context)) {
            return
        }

        if (!isMetronomeServiceConnected) {
            context.startForegroundService(Intent(context, MetronomeService::class.java))

            Intent(context, MetronomeService::class.java).also { intent ->
                context.bindService(intent, connection, 0)
            }
        }
    }

    fun tryStopMetronome() {
        if (!isPlaying) {
            metronomeService?.stopForegroundService()
        }
    }

    fun requestNotificationPermission() {
        viewModelScope.launch {
            _requestNotificationPermissionFlow.emit(Unit)
        }
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
        metronomeService?.changeAccent(!isAccent)
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
                screenFlow.emit(it.toMetronomeScreenState())
            }
        }
    }

    private fun onServiceDisconnected() {
        collectSoundEngineStateJob?.cancel()
    }

    companion object {

        private const val TAG = "MainViewModel_TAG"
    }

    private fun SoundEngineState.toMetronomeScreenState() = ScreenState.Metronome(
        isPlaying = isPlaying,
        bpm = bpm,
        numerator = numerator,
        denominator = denominator,
        accent = accent,
        subbeat = subbeat
    )
}