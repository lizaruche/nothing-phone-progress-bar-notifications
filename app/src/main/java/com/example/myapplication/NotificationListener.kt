package com.example.myapplication

import android.content.ComponentName
import android.content.Intent
import android.provider.ContactsContract
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import java.util.Collections
import androidx.lifecycle.ViewModelProvider

class NotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "NotificationListener"
        private val notificationsWithProgressBar = mutableSetOf<String>()
        var viewModel: NotificationsViewModel? = null

        fun getNotifications(): Set<String> {
            return notificationsWithProgressBar
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "NotificationListener created")
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(NotificationsViewModel::class.java)
    }


//    private val notificationsWithProgressBar = Collections.synchronizedSet(mutableSetOf<String>())

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification listener connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Notification listener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification
        // && notification.extras.containsKey(android.app.Notification.EXTRA_PROGRESS)
        if (notification != null ) {
            val key = sbn.key
            notificationsWithProgressBar.add(key)
            viewModel?.addNotification(key)
            Log.d(TAG, "Added notification with progress bar: $key")
            Log.d(TAG, "New set is: $notificationsWithProgressBar")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val key = sbn.key
        if (notificationsWithProgressBar.contains(key)) {
            notificationsWithProgressBar.remove(key)
            viewModel?.removeNotification(key)
            Log.d(TAG, "Removed notification with progress bar: $key")
        }
    }

    fun getNotificationsWithProgressBar(): Set<String> {
        Log.d(TAG, "Set is: $notificationsWithProgressBar")
        return synchronized(notificationsWithProgressBar) {
            notificationsWithProgressBar.toSet()
        }
    }
}