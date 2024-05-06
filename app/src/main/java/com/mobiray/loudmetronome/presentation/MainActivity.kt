package com.mobiray.loudmetronome.presentation

import android.Manifest
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mobiray.loudmetronome.presentation.ui.theme.LoudMetronome3Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val component by lazy {
        (application as MetronomeApplication).component
    }

    private val mainViewModel by lazy {
        ViewModelProvider(
            owner = this,
            factory = component.getViewModelFactory()
        )[MainViewModel::class.java]
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { success ->
            if (success) {
                mainViewModel.handlePermissionRequest()
            } else {
                openAppSettings()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val screenState = mainViewModel.screenStateFlow.collectAsState()

            LoudMetronome3Theme {
                when (val screenStateValue = screenState.value) {

                    is ScreenState.Loading -> LoadingScreen()

                    is ScreenState.RequestPermission -> RequestPermissionScreen(
                        onClickContinue = {
                            mainViewModel.requestNotificationPermission()
                        }
                    )

                    is ScreenState.Metronome -> MetronomeSkinScreen(
                        screenState = screenStateValue,
                        onClickPlayStop = {
                            mainViewModel.playStop()
                        },
                        onClickAddBpm = {
                            mainViewModel.addBpm(it)
                        },
                        onClickNumerator = {
                            mainViewModel.changeNumerator(it)
                        },
                        onClickDenominator = {
                            mainViewModel.changeDenominator(it)
                        },
                        onClickAccent = {
                            mainViewModel.changeAccent()
                        },
                        onClickSubbeat = {
                            mainViewModel.changeSubbeat(it)
                        },
                        onTapTempo = {
                            mainViewModel.tapTempo()
                        }
                    )
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
                mainViewModel.requestNotificationPermissionFlow.collect {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        mainViewModel.handlePermissionRequest()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        mainViewModel.tryStartMetronomeService()
    }

    override fun onStop() {
        super.onStop()

        mainViewModel.tryStopMetronomeService()
    }

    private fun openAppSettings() {
        startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            setData(Uri.fromParts("package", packageName, null))
            addCategory(CATEGORY_DEFAULT)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            addFlags(FLAG_ACTIVITY_NO_HISTORY)
            addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        })
    }

    companion object {

        private const val TAG = "MainActivity_TAG"
    }
}