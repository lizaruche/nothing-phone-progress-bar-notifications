package com.example.myapplication

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MainActivity.Companion

class BatteryActivity : ComponentActivity() {

    private lateinit var glyphController: GlyphController
    private lateinit var batteryController: BatteryController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_battery)

        glyphController = GlyphController(this)
        batteryController = BatteryController(this)

        displayBatteryLevel()
    }

    private fun displayBatteryLevel() {
        val cBatLvl = batteryController.getCurrentBatteryLevel()
        Log.d(TAG, "current battery lvl: $cBatLvl%")

        glyphController.toggleProgressPeriod(cBatLvl)
    }

    companion object {
        private const val TAG = "BatteryActivity"
    }
}
