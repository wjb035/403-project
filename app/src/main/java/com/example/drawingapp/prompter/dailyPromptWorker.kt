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
            val wordFetcher = WordFetch()
            val prompt = wordFetcher.getDrawingPrompt()
            val drawData = booleanPreferencesKey("drawData")
            appContext.dataStore.edit { settings ->
                settings[drawData] = true
            }
            // Save daily prompt to SharedPreferences
            val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            prefs.edit {
                putString("daily_prompt", prompt)
                putString("last_prompt_date", today)
            }

            scheduleNextPrompt(appContext)
            val work2 = OneTimeWorkRequestBuilder<workLock>()
                .setInitialDelay(1, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(appContext).beginUniqueWork(
                "dailyPromptWork",
                ExistingWorkPolicy.REPLACE,
                work2
            ).enqueue()
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
                val startHour = 9
                val endHour = 17
                val randomHour = (startHour until endHour).random()
                val lockHour = randomHour + 1
                //val randomMinute = (0 until 59).random()
                val randomMinute = 1

                // Trigger the prompt whenever this time is reached
                val calendar =  Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, randomHour)
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

class workLock (private val appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            val drawData = booleanPreferencesKey("drawData")
            appContext.dataStore.edit { settings ->
                settings[drawData] = false
            }
            //scheduleNextPrompt(appContext)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}