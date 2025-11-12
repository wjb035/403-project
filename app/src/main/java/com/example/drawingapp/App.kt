package com.example.drawingapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.drawingapp.prompter.ScheduleDailyPrompt

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id",
                "Channel name",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        ScheduleDailyPrompt(applicationContext).schedule()
    }
}