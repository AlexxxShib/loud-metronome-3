package com.mobiray.loudmetronome.tools

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mobiray.loudmetronome.R
import com.mobiray.loudmetronome.presentation.MainActivity

private const val NOTIFICATION_CHANNEL_ID = "general_notification_channel"

fun createNotificationChannel(context: Context) {
    val notificationManager =
        context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

    val channel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        context.getString(R.string.metronome_service_notification_channel_general_name),
        NotificationManager.IMPORTANCE_DEFAULT
    )
    notificationManager.createNotificationChannel(channel)
}

fun buildNotification(context: Context): Notification {
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(context.getString(R.string.metronome_service_notification_title))
        .setContentText(context.getString(R.string.metronome_service_notification_description))
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setSilent(true)
        .setOngoing(true)
        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        .build()

    notification.flags = notification.flags or Notification.FLAG_NO_CLEAR

    return notification
}

fun checkNotificationPermissionRequired(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        val permissionStatus = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        )

        return permissionStatus != PackageManager.PERMISSION_GRANTED
    }

    return false
}