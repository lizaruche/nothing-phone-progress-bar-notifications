package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class BatteryService : Service() {

    private val binder = LocalBinder()
    private lateinit var batteryReceiver: BatteryReceiver
    private lateinit var glyphController: GlyphController

    override fun onCreate() {
        super.onCreate()
        glyphController = GlyphController(this)
        batteryReceiver = BatteryReceiver(glyphController)
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            RECEIVER_NOT_EXPORTED
        )
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryReceiver.glyphController.turnOffGlyph()
        batteryReceiver.glyphController.close()
        unregisterReceiver(batteryReceiver)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun getBatteryStatus(): Float {
        return batteryReceiver.batteryPct
    }

    private fun createNotification(): Notification {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("battery_service", "Battery Service")
        } else {
            ""
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Battery Monitoring Service")
            .setContentText("Monitoring battery status...")
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
        }
        return channelId
    }

    inner class LocalBinder : Binder() {
        fun getService(): BatteryService = this@BatteryService
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}
