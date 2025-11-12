package com.example.drawingapp.ui.profile

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.drawingapp.model.User
import com.example.drawingapp.model.UserViewModel
import com.example.drawingapp.network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun OtherProfileScreenWrapper(
    navController: NavController,
    userViewModel: UserViewModel,
    userId: Long
) {
    var user by remember { mutableStateOf<User?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(userId) {
        scope.launch {
            try {
                user = RetrofitInstance.userApi.getUserById(userId)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    user?.let {
        OtherProfileScreen(navCon = navController, userViewModel = userViewModel, user = it)
    }
}