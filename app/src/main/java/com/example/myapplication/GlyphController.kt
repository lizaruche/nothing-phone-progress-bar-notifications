package com.example.myapplication

import android.content.ComponentName
import android.content.Context
import android.util.Log
import com.example.myapplication.BatteryReceiver.Companion
import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph
import com.nothing.ketchum.GlyphException
import com.nothing.ketchum.GlyphFrame
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GlyphController(private val context: Context) {

    private val glyphManager: GlyphManager = GlyphManager.getInstance(context)
    var curProg = 0

    init {
        Log.i(TAG, "start init")
        val glyphCallback = object : GlyphManager.Callback {
            override fun onServiceConnected(componentName: ComponentName) {
                if (Common.is20111()) glyphManager.register(Common.DEVICE_20111)
                if (Common.is22111()) glyphManager.register(Common.DEVICE_22111)
                if (Common.is23111()) glyphManager.register(Common.DEVICE_23111)
                try {
                    glyphManager.openSession()
                } catch (e: GlyphException) {
                    Log.e(TAG, e.message ?: "Unknown error")
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                glyphManager.closeSession()
            }
        }
        Log.i(TAG, "start init glyph")
        glyphManager.init(glyphCallback)
    }

    fun close() {
        try {
            glyphManager.closeSession()
        } catch (e: GlyphException) {
            Log.e(TAG, e.message ?: "Unknown error")
        }
        Log.d(TAG, "closing")
        glyphManager.unInit()
    }

    fun turnOffGlyph() {
        Log.d(TAG, "Turn off glyphs")
        glyphManager.turnOff()
    }

    fun toggleProgressPeriod(cur: Int = curProg, period: Long = 3_000) {
        if (cur !in 0 .. 100) {
            throw Exception("cur must be between 0 and 100. given $cur")
        }
        Log.d(TAG, "Toggle progress $cur out of 100")

        val frameE = glyphManager.glyphFrameBuilder
            .buildChannel(Glyph.Code_20111.E1, 1_000)
            .buildInterval(10)
            .buildCycles(2).buildPeriod(3000)
            .build()
        val frameED = glyphManager.glyphFrameBuilder
            .buildChannelE()
            .buildChannelD()
            .buildInterval(5)
            .buildCycles(1).buildPeriod(3000)
            .build()

        runBlocking {
            val job = GlobalScope.launch {
                if (cur <= 20) {
                    glyphManager.toggle(frameE)
                } else {
                    glyphManager.displayProgressAndToggle(frameED, cur - 20, false)
                }
                Thread.sleep(period)
                glyphManager.turnOff()
            }
        }
    }

    fun toggleProgress(cur: Int = curProg) {
        if (cur !in 0 .. 100) {
            throw Exception("cur must be between 0 and 100. given $cur")
        }
        Log.d(TAG, "Toggle progress $cur out of 100")


        val frameE = glyphManager.glyphFrameBuilder
            .buildChannel(Glyph.Code_20111.E1, 1_000)
            .build()
        val frameED = glyphManager.glyphFrameBuilder
            .buildChannelE()
            .buildChannelD()
            .build()

        if (cur <= 20) {
            glyphManager.toggle(frameE)
        } else {
            glyphManager.displayProgressAndToggle(frameED, cur - 20, false)
        }
    }

    companion object {
        private const val TAG = "GlyphController"
    }
}