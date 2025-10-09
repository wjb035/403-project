package com.example.drawingapp.prompter

import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar
import android.content.Context

class ScheduleDailyPrompt(private val context: Context) {
    fun schedule(){
        val startHour = 9
        val endHour = 14
        val randomHour = (startHour until endHour).random()
        val randomMinute = (0 until 59).random()


        // Trigger the prompt whenever this time is reached
        val calendar =  Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, randomHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val delay = calendar.timeInMillis - System.currentTimeMillis()
        val initialDelay = if (delay > 0) delay else delay + TimeUnit.DAYS.toMillis(1)

        val dailyWork = PeriodicWorkRequestBuilder<DailyPromptWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "dailyPromptWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyWork
        )

    }
    // Get a random time between a certain interval each day

}


