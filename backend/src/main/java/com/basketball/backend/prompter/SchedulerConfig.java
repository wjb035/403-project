package com.basketball.backend.config

import com.basketball.backend.controller.PromptController
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Configuration
@EnableScheduling