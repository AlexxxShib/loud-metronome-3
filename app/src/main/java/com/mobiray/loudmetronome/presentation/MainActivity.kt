package com.mobiray.loudmetronome.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {

    private val notificationPermissionAccepted = mutableStateOf(false)

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            checkAndRequestNotificationPermission()
        }

    private val mainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            if (notificationPermissionAccepted.value)
            {
                MetronomeScreen()
            }
        }

        checkAndRequestNotificationPermission()
    }

    override fun onStart() {
        super.onStart()

        mainViewModel.tryStartMetronomeService()
    }

    override fun onStop() {
        super.onStop()

        mainViewModel.tryStopMetronomeService()
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val permissionStatus = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS)

            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                notificationPermissionAccepted.value = true
            }
            else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    companion object {

        private const val TAG = "MainActivity_TAG"
    }
}