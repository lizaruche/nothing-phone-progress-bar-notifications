package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var glyphController: GlyphController
    private lateinit var batteryController: BatteryController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                ServiceControlUI()
            }
        }
//        enableEdgeToEdge()
//        checkNotificationPermission()
//        startForegroundService(Intent(this, BatteryService::class.java))
//        setContent {
//            MyApplicationTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(innerPadding),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Button(onClick = {
//                            startForegroundService(Intent(this, BatteryService::class.java))
//                        }) {
//                            Text(text = "Start Service")
//                        }
//                        Button(onClick = {
//                            val stopIntent = Intent(context, BatteryService::class.java)
//                            context.stopService(stopIntent)
//                        }, modifier = Modifier.padding(top = 16.dp)) {
//                            Text(text = "Stop Service")
//                        }
//                    }
//                }
//            }
//        }
//        startForegroundService(Intent(this, BatteryService::class.java))
//        finish()
    //
//        glyphController = GlyphController(this)
//        batteryController = BatteryController(this)
//
//        handleIntent(intent)
//        createShortcuts()
//        finish()
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            ACTION_DISPLAY_BATTERY_LEVEL -> displayBatteryLevel()
            ACTION_TURN_OFF_GLYPHS -> turnOffGlyphs()
        }
    }

    private fun displayBatteryLevel() {
        val batteryLevel = batteryController.getCurrentBatteryLevel()
        Log.d(TAG, "Battery Level: $batteryLevel")
        glyphController.toggleProgressPeriod(batteryLevel)
    }

    private fun turnOffGlyphs() {
        glyphController.turnOffGlyph()
    }

    private fun createShortcuts() {
        val displayBatteryShortcut = ShortcutInfoCompat.Builder(this, "display_battery")
            .setShortLabel("Display Battery Level")
            .setLongLabel("Display Battery Level on Glyphs")
//            .setIcon(IconCompat.createWithResource(this, R.drawable.ic_battery))
            .setIntent(Intent(this, MainActivity::class.java).setAction(ACTION_DISPLAY_BATTERY_LEVEL))
            .build()

        val turnOffGlyphsShortcut = ShortcutInfoCompat.Builder(this, "turn_off_glyphs")
            .setShortLabel("Turn Off Glyphs")
            .setLongLabel("Turn Off Glyphs")
//            .setIcon(IconCompat.createWithResource(this, R.drawable.ic_power))
            .setIntent(Intent(this, MainActivity::class.java).setAction(ACTION_TURN_OFF_GLYPHS))
            .build()

        ShortcutManagerCompat.addDynamicShortcuts(this, listOf(displayBatteryShortcut, turnOffGlyphsShortcut))
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val ACTION_DISPLAY_BATTERY_LEVEL = "com.example.myapplication.DISPLAY_BATTERY_LEVEL"
        private const val ACTION_TURN_OFF_GLYPHS = "com.example.myapplication.TURN_OFF_GLYPHS"
    }
}


@Composable
fun ServiceControlUI() {

    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            val startIntent = Intent(context, BatteryService::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }) {
            Text(text = "Start Service")
        }
        Button(onClick = {
            val stopIntent = Intent(context, BatteryService::class.java)
            context.stopService(stopIntent)
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text(text = "Stop Service")
        }
    }
}