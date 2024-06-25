package com.example.myapplication

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.lazy.items


class MainActivity : ComponentActivity() {

    private lateinit var glyphController: GlyphController
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var notificationListener: NotificationListener
    private var textState by mutableStateOf(TextFieldValue("0"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glyphController = GlyphController(this)
        notificationHelper = NotificationHelper(this)
        notificationListener = NotificationListener()
        requestNotificationListenerPermission()

        val viewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]
        NotificationListener.viewModel = viewModel
        enableEdgeToEdge()
//        checkNotificationPermission()
        setContent {
            MyApplicationTheme {
                val notifications by viewModel.notificationsWithProgressBar.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProgressInput(textState) { newText -> onInputTextChange(newText) }
                        Button(onClick = { glyphController.toggleProgress() }) {
                            Text("Display Progress")
                        }
                        Button(onClick = { animateProgress() }) {
                            Text("Animate Progress with Notification")
                        }
                        Button(onClick = { printNotifications() }) {
                            Text("Print notifications to log")
                        }
                        Button(onClick = { glyphController.turnOffGlyph() }) {
                            Text("Turn Off")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Notifications with Progress:")
                        NotificationsList(viewModel)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Разрешение предоставлено, можно отправлять уведомления
            } else {
                checkNotificationPermission()
            }
        }
    }

    private fun requestNotificationListenerPermission() {
        val componentName = ComponentName(this, NotificationListener::class.java)
        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(componentName.packageName)) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
    }

    private fun checkNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1
        )
    }

    override fun onDestroy() {
        Log.i(TAG, "closing glyphController")
        glyphController.close()
        super.onDestroy()
    }

    private fun animateProgress() {
        Thread(Runnable{
            SystemClock.sleep(2000)
            var progress = 0
            while (progress <= 100) {
                notificationHelper.showProgressNotification(progress)
                glyphController.toggleProgress(progress)
                progress += 10
                SystemClock.sleep(
                    1000
                )
            }

        }).start()
    }

    private fun printNotifications() {
//        val nots = notificationListener.getNotificationsWithProgressBar()
        val nots = NotificationListener.getNotifications()
        Log.i(TAG, "notifications: $nots")
    }

    private fun onInputTextChange(newText: TextFieldValue) {
        val newCurProg = newText.text.toIntOrNull() ?: 0
        Log.d(TAG, "new cur prog $newCurProg")
        glyphController.curProg = newCurProg.coerceIn(0, 100)
        textState = newText.copy(text = glyphController.curProg.toString())
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
fun NotificationsList(viewModel: NotificationsViewModel) {
    val notifications by viewModel.notificationsWithProgressBar.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notifications) { notification ->
            Text(notification, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun ProgressInput(textState: TextFieldValue, onTextChange: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = textState,
        onValueChange = { newText ->
            if (newText.text.all { it.isDigit() }) {
                onTextChange(newText)
        } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp))
}