package com.example.drawingapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.drawingapp.model.User

class UserViewModel : ViewModel() {
    var currentUser by mutableStateOf<User?>(null)
        private set

    fun setUser(user: User) {
        currentUser = user
    }

    fun getUser(): User? {
        return currentUser
    }

    fun clearUser() {
        currentUser = null
    }
}