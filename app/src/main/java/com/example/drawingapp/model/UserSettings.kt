package com.example.drawingapp.model

import kotlinx.serialization.Serializable

// This file represents all of the data returned by the Spring Boot API
data class UserSettings (
    var id: Long? = null,
    val userId: Long,
    val theme: String? = "light",
    val notificationsEnabled: Boolean? = true,
    val mostRecentFollowerId: Long? = null,
    val mostRecentFollowerPostId: Long? = null,
    val mostRecentLikedPostId: Long? = null
)