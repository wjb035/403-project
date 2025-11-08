package com.example.drawingapp.model

data class MinimalUser(
    val id: Long,
    val username: String
)
data class Drawing(
    val id: Long,
    val imageUrl: String,
    var likesCount: Int,
    val user: User
)


