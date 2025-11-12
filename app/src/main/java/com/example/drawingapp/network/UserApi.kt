package com.example.drawingapp.network;

import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.User;
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// 1. app retrieves info
// 2. adds it to the user object (serialized to JSON)
// 3. send user object to the backend java server (via retrofit)

public interface UserApi {

    // Register new user
    @POST("/api/users/register")
    suspend fun register(@Body user: User): User

    // Login user
    @POST("/api/users/login")
    suspend fun login(@Body credentials: Map<String, String>): User

    // Display all users
    @GET("/api/users/display")
    suspend fun getAllUsers(): List<User>

    // Searches for a specfiic user, not case sensitive
    @GET("/api/users/search/}")
    suspend fun searchUsers(@Query("query") query: String): List<User>

    // Upload a pfp
    @Multipart
    @POST("/api/users/uploadProfilePicture")
    suspend fun uploadProfilePicture(
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
    ): User

    @POST("api/users/follow/{username}")
    suspend fun followUser(@Path("username") username: String, @Body user: User) : User

    @POST("api/users/unfollow/{username}")
    suspend fun unfollowUser(@Path("username") username: String, @Body user: User) : User

}
