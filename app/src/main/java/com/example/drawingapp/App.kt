package com.example.drawingapp

import android.app.Application
import com.example.drawingapp.prompter.ScheduleDailyPrompt

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        ScheduleDailyPrompt(applicationContext).schedule()
    }
}