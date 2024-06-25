package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val channelId = "progress_channel"
    private val notificationId = 1

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Progress Channel"
        val descriptionText = "Channel for progress notifications"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showProgressNotification(progress: Int) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Progress")
            .setContentText("Progress: $progress%")
            .setSmallIcon(R.drawable.ic_notification_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(100, progress, false)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf("android.permission.POST_NOTIFICATIONS"),
                1
            )
            return
        }
        notificationManager.notify(notificationId, builder.build())
    }

    fun completeNotification() {
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Progress")
            .setContentText("Complete")
            .setSmallIcon(R.drawable.ic_notification_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, false)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf("android.permission.POST_NOTIFICATIONS"),
                1
            )
            return
        }
        notificationManager.notify(notificationId, builder.build())
    }
}
