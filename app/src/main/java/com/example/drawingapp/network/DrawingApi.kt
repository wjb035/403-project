package com.example.drawingapp.network

import com.example.drawingapp.model.Drawing
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Path
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part


interface DrawingApi {
    // Get leaderboard by likes
    @GET("api/drawings/leaderboard/likes")
    suspend fun getLeaderboardByLikes(): List<Drawing>

    // Get leaderboard by new
    @GET("api/drawings/leaderboard/new")
    suspend fun getLeaderboardByNew(): List<Drawing>

    // Get all drawings from user
    @GET("api/drawings/user/{userId}")
    suspend fun getUserDrawings(@Path("userId") userId: Long): List<Drawing>

    // Like a drawing
    @POST("api/drawings/like/{drawingId}/{userId}")
    suspend fun likeDrawing(
        @Path("drawingId") drawingId: Long,
        @Path("userId") userId: Long
    ): Drawing

    // Dislike a drawing
    @POST("api/drawings/unlike/{drawingId}/{userId}")
    suspend fun unlikeDrawing(
        @Path("drawingId") drawingId: Long,
        @Path("userId") userId: Long
    ): Drawing

    // Upload a drawing
    @Multipart
    @POST("/api/drawings/uploadDrawing")
    suspend fun uploadDrawing(
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("promptId") promptId: RequestBody
    ): Drawing
}