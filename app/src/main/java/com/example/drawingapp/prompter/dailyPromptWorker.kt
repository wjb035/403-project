package com.example.drawingapp.prompter

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import androidx.work.ExistingWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.drawingapp.prompter.DailyPromptWorker.Companion.scheduleNextPrompt
import com.example.drawingapp.ui.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class DailyPromptWorker (private val appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params){
    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {

            val drawData = booleanPreferencesKey("drawData")
            appContext.dataStore.edit { settings ->
                settings[drawData] = false
            }



            scheduleNextPrompt(appContext)


            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        fun scheduleNextPrompt(context: Context, testing: Boolean = false) {
            val delay: Long = if (testing) {
                0L
            } else {

                val calendar =  Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    //set(Calendar.HOUR_OF_DAY, 21)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                var calculatedDelay = calendar.timeInMillis - System.currentTimeMillis()
                if (calculatedDelay < 0) calculatedDelay += TimeUnit.DAYS.toMillis(1)
                calculatedDelay
            }



            val work = OneTimeWorkRequestBuilder<DailyPromptWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).beginUniqueWork(
                "dailyPromptWork",
                ExistingWorkPolicy.REPLACE,
                work
            ).enqueue()


        }
    }

}
