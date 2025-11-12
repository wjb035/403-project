package com.example.drawingapp.network;

import com.example.drawingapp.model.User;

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
}
