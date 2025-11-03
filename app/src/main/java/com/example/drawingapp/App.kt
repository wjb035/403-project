package com.example.drawingapp

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.drawingapp.prompter.ScheduleDailyPrompt

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        ScheduleDailyPrompt(applicationContext).schedule()
    }
}