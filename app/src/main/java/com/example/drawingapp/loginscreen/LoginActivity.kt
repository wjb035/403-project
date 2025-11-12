package com.example.drawingapp.loginscreen

// Android imports

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drawingapp.R
import com.example.drawingapp.model.User
import com.example.drawingapp.network.UserApi
import com.example.drawingapp.network.RetrofitInstance
import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.drawingapp.model.UserViewModel
import kotlinx.coroutines.launch


/**
 * LoginActivity
 * This screen allows the user to enter a username and password,
 * sends those credentials to spring boot API thru Retrofit,
 * handles success/failure response.
 */

@Composable
fun LoginScreen(navCon: NavController, userViewModel: UserViewModel) {
    WhiteboardSimTheme {
        // declare UI components
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        // Background
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.white_layer),
                contentDescription = "background",
                //contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "QuickDraw Logo"
            )
            Spacer(Modifier.height(16.dp))

            Text(
                "Login",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val loggedInUser = RetrofitInstance.userApi.login(
                                mapOf("username" to username, "password" to password)
                            )
                            userViewModel.setUser(loggedInUser)
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            navCon.navigate("splash") {
                                popUpTo("login") { inclusive = true }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Invalid credentials or network error", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                //modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            ) {
                Text("Login")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { navCon.navigate("register") }) {
                Text(
                    "No account? Register",
                    color = MaterialTheme.colorScheme.secondary
                )

            }

        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {

    val userViewModel = remember { UserViewModel() }
    LoginScreen(
        navCon = NavController(LocalContext.current),
        userViewModel = userViewModel
    )
}
