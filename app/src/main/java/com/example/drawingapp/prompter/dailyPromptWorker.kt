package com.example.drawingapp.prompter
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit

class DailyPromptWorker (context: Context, params: WorkerParameters): CoroutineWorker(context, params){
    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            val wordFetcher = WordFetch()
            val prompt = wordFetcher.getDrawingPrompt()

            // Save daily prompt to SharedPreferences
            val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            prefs.edit {
                putString("daily_prompt", prompt)
                    .putString("last_prompt_date", today)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}