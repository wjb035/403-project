package com.example.drawingapp.network

import retrofit2.http.GET
import com.example.drawingapp.model.Prompt

interface PromptApi {
    @GET("api/prompts/today")
    suspend fun getTodaysPrompt(): Prompt


}