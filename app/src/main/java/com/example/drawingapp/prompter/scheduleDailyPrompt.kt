package com.example.drawingapp.prompter

import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar
import android.content.Context

class ScheduleDailyPrompt(private val context: Context) {
    fun schedule() {
        DailyPromptWorker.scheduleNextPrompt(context, testing = true)
    }
}


