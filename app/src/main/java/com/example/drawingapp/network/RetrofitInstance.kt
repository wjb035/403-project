package com.example.drawingapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitInstance {
    private const val BASE_URL = "https://quickdraw-backend-ipi7.onrender.com"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val drawingApi: DrawingApi by lazy { retrofit.create(DrawingApi::class.java) }

    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val promptApi: PromptApi by lazy { retrofit.create(PromptApi::class.java) }


}