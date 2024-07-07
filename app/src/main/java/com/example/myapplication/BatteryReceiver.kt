package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import kotlinx.coroutines.Delay

class BatteryReceiver(val glyphController: GlyphController) : BroadcastReceiver() {

    companion object {
        private const val TAG = "BatteryReceiver"
    }

    var batteryPct: Float = -1f
        private set

    var isCharging: Boolean = false
        private set

    override fun onReceive(context: Context, intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val newBatteryPct = level / scale.toFloat() * 100
        Log.d(TAG, "Battery level: $batteryPct%")
        if (newBatteryPct != batteryPct) {
            Log.d(TAG, "Battery level: changed")
            Log.d(TAG, "Battery level: $batteryPct%")
            batteryPct = newBatteryPct
//            displayBatteryOnGlyphs()
        }
    }

    fun displayBatteryOnGlyphs(period: Long = 5_000, delay: Long = 0) {
        Log.d(TAG, "Display: $batteryPct%")
        glyphController.toggleProgressPeriod(batteryPct.toInt(), period, delay)
    }
}
