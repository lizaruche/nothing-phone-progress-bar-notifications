package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

class BatteryService : Service(), SensorEventListener {

    private val binder = LocalBinder()
    private lateinit var batteryReceiver: BatteryReceiver
    private lateinit var glyphController: GlyphController
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var proximitySensor: Sensor? = null
    private var x: Float = 0.0F
    private var y: Float = 0.0F
    private var z: Float = 0.0F
    private var block = false


    private var isFaceDown = false

    override fun onCreate() {
        super.onCreate()
        glyphController = GlyphController(this)
        batteryReceiver = BatteryReceiver(glyphController)
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            RECEIVER_NOT_EXPORTED
        )

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }
    override fun onDestroy() {
        super.onDestroy()

        sensorManager.unregisterListener(this)
        batteryReceiver.glyphController.turnOffGlyph()
        batteryReceiver.glyphController.close()
        unregisterReceiver(batteryReceiver)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> handleAccelerometerEvent(it)
                Sensor.TYPE_PROXIMITY -> handleProximityEvent(it)
                else -> {}
            }
        }
    }

    private fun handleAccelerometerEvent(event: SensorEvent) {
        val newX = event.values[0]
        val newY = event.values[1]
        val newZ = event.values[2]

        if (isFaceDown && !block) {
            val movementThreshold = 0.6
            if (abs(abs(x) - abs(newX)) > movementThreshold || abs(abs(y) - abs(newY)) > movementThreshold || abs(abs(z) - abs(newZ)) > movementThreshold) {
                runBlocking {
                    GlobalScope.launch {
                        Log.d(TAG, "change state")
                        block = true
                        batteryReceiver.displayBatteryOnGlyphs(2_000)
                        Thread.sleep(5_000)
                        block = false
                        Log.d(TAG, "continue listening")
                    }
                }
            }
        }
        x = newX
        y = newY
        z = newZ
    }


    private fun handleProximityEvent(event: SensorEvent) {
        isFaceDown = event.values[0] < (proximitySensor?.maximumRange ?: 0f)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
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
        private const val TAG = "BatteryService"
    }
}
