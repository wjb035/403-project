package com.example.drawingapp.network

import retrofit2.http.GET

interface canDrawApi {
    @GET("api/prompts/time")
    suspend fun getTime(): Boolean
}