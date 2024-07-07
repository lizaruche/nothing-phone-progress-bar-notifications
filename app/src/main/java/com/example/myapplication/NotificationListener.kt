package com.github.chagall.notificationlistenerexample

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * MIT License
 *
 * Copyright (c) 2016 Fábio Alves Martins Pereira (Chagall)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class NotificationListenerExampleService : NotificationListenerService() {
    /*
           These are the package names of the apps. for which we want to
           listen the notifications
        */
    private var curKey: String? = null

    companion object {
        private const val TAG = "NotificationListenerES"
    }

    private object ApplicationPackageNames {
        const val YANDEX_GO_PACK_NAME: String = "com.example.myapplication"
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "Service bind")
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notificationProgress = getNotificationProgress(sbn)
        if (curKey.isNullOrBlank()) curKey = sbn.key
        Log.d(TAG, "curkey: $curKey")

        if (notificationProgress > 0 && curKey == sbn.key) {
            Log.d(TAG, "notification progress: $notificationProgress")
            val intent = Intent("com.github.chagall.notificationlistenerexample")
            intent.putExtra("Notification Progress", notificationProgress)
            sendBroadcast(intent)
//            val intent = Intent("com.github.chagall.notificationlistenerexample")
//            intent.putExtra("Notification Code", notificationProgress)
//            sendBroadcast(intent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (curKey == sbn.key) {
            Log.d(TAG, "removing curKey: $curKey")
            curKey = null
            // turn off glyph
        }
    }

    private fun getNotificationProgress(sbn: StatusBarNotification): Int {
        val packageName = sbn.packageName

        return if (packageName == ApplicationPackageNames.YANDEX_GO_PACK_NAME
            && sbn.notification.extras.getInt(Notification.EXTRA_PROGRESS) != 0) {
            Math.round(
                sbn.notification.extras.getInt(Notification.EXTRA_PROGRESS).toDouble()
                        / sbn.notification.extras.getInt(Notification.EXTRA_PROGRESS_MAX)
                        * 100).toInt()
        } else {
            -1
        }
    }
}