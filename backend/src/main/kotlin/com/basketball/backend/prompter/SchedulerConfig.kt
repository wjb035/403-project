package com.basketball.backend.prompter

import com.basketball.backend.controller.PromptController
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Configuration
@EnableScheduling
public class SchedulerConfig(private val promptController: PromptController) {

    private val scheduler = ThreadPoolTaskScheduler().apply {
        poolSize = 1
        initialize()
    }

    private var future: ScheduledFuture<*>? = null

    // demo mode
    private val demoMode = true
    private val demoIntervalSeconds = 60L

    init { scheduleNextPrompt() }

    // Scheudles the prompt form a random hour or minute
    private fun scheduleNextPrompt() {
        val delay = if (demoMode) {
            TimeUnit.SECONDS.toMillis(demoIntervalSeconds)
        } else{
            val now = Calendar.getInstance()
            val randomHour = (9..17).random()
            val randomMinute = (0..59).random()

            val nextRun = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, randomHour)
                set(Calendar.MINUTE, randomMinute)
                set(Calendar.SECOND, 0)
                if (timeInMillis <= now.timeInMillis) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            nextRun.timeInMillis - now.timeInMillis
        }


        future ?.cancel(false) // cancel previous task if it exists
        future = scheduler.schedule({
                val prompt = promptController.generateDailyPrompt()

                // Generate prompt delay
                scheduler.schedule({
                    promptController.closeDrawingWindow(prompt.id)
                }, Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis((1))))

                scheduleNextPrompt() // schedule next day
        }, Date(System.currentTimeMillis() + delay))

        println(
            if (demoMode)
                "Demo: Next prompt in $demoIntervalSeconds seconds"
            else
                "Next daily prompt scheduled"
        )
    }

}