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
class SchedulerConfig(private val promptController: PromptController) {

    private val scheduler = ThreadPoolTaskScheduler().apply {
        poolSize = 1
        initialize()
    }

    private var future: ScheduledFuture<*>? = null

    init { scheduleNextPrompt() }

    // Scheudles the prompt form a random hour or minute
    private fun scheduleNextPrompt() {
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

        val delay = nextRun.timeInMillis - now.timeInMillis

        future ?.cancel(false) // cancel previous task if it exists
        future = scheduler.schedule({
                promptController.generateDailyPrompt()
                scheduleNextPrompt() // schedule next day
        }, Date(System.currentTimeMillis() + delay))

        println("Next daily prompt scheduled at: ${nextRun.time}")
    }

}