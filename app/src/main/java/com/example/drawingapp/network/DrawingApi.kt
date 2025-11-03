package com.example.drawingapp.network

import com.example.drawingapp.model.Drawing
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface DrawingApi {
    @GET("api/users/leaderboard")
    suspend fun getLeaderboard(): List<Drawing>

    @POST("api/users/like/{drawingId}")
    suspend fun likeDrawing(@Path("drawingId") drawingId: Long): Drawing

    @POST("api/users/unlike/{drawingId}")
    suspend fun unlikeDrawing(@Path("drawingId") drawingId: Long): Drawing

}