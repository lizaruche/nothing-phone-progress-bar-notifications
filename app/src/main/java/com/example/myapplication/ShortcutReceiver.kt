package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ShortcutReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received")
        val glyphController = GlyphController(context)
        val batteryController = BatteryController(context)

        when (intent.action) {
            ACTION_DISPLAY_BATTERY_LEVEL -> displayBatteryLevel(glyphController, batteryController)
            ACTION_TURN_OFF_GLYPHS -> turnOffGlyphs(glyphController)
        }
    }

    private fun displayBatteryLevel(glyphController: GlyphController, batteryController: BatteryController) {
        val batteryLevel = batteryController.getCurrentBatteryLevel()
        Log.d(TAG, "Battery Level: $batteryLevel")
        glyphController.toggleProgressPeriod(batteryLevel)
    }

    private fun turnOffGlyphs(glyphController: GlyphController) {
        glyphController.turnOffGlyph()
    }

    companion object {
        private const val TAG = "ShortcutReceiver"
        const val ACTION_DISPLAY_BATTERY_LEVEL = "com.example.myapplication.DISPLAY_BATTERY_LEVEL"
        const val ACTION_TURN_OFF_GLYPHS = "com.example.myapplication.TURN_OFF_GLYPHS"
    }
}
