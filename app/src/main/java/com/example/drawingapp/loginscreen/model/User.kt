package com.example.drawingapp.loginscreen.model

// This file represents all of the data returned by the Spring Boot API
data class User (
    var id: Long? = null,
    var username: String = "",
    var password: String = "",
    var bio: String = "",
    var profilePicture: String = "",
    var createdAt: String = "",
    var followers: List<User> = emptyList()
)