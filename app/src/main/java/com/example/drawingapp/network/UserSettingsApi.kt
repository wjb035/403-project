package com.example.drawingapp.network;

import com.example.drawingapp.model.UserSettings
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// 1. app retrieves info
// 2. adds it to the user object (serialized to JSON)
// 3. send user object to the backend java server (via retrofit)

public interface UserSettingsApi {

    // Get user settings by userId
    @GET("/api/settings/{userId}/get")
    suspend fun getUserSettings(@Path("userId") userId: Long): UserSettings

    // Update user settings
    @POST("/api/settings/{userId}/update")
    suspend fun updateUserSettings(
        @Path("userId") userId: Long,
        @Body newSettings: UserSettings
    ): UserSettings

    // Delete user account (and settings)
    @DELETE("/api/settings/{userId}/delete")
    suspend fun deleteUser(@Path("userId") userId: Long): String

}
