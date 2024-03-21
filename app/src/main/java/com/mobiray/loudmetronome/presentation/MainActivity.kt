package com.mobiray.loudmetronome.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.mobiray.loudmetronome.soundengine.sample.SampleLoader

class MainActivity : ComponentActivity() {

    private val notificationPermissionAccepted = mutableStateOf(false)

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            checkAndRequestNotificationPermission()
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