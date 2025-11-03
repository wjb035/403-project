package com.example.drawingapp.loginscreen

// Android imports

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drawingapp.R
import com.example.drawingapp.loginscreen.model.User
import com.example.drawingapp.loginscreen.network.ApiService
import com.example.drawingapp.loginscreen.network.RetrofitClient
import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * LoginActivity
 * This screen allows the user to enter a username and password,
 * sends those credentials to spring boot API thru Retrofit,
 * handles success/failure response.
 */

@Composable
fun LoginScreen(navCon: NavController) {
    WhiteboardSimTheme {
        // declare UI components
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val context = LocalContext.current

        // Create a retrofit API client from RetrofitClient class
        val api = remember { RetrofitClient.getClient().create(ApiService::class.java) }

        // Background
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.white_layer),
                contentDescription = "background",
                contentScale = ContentScale.FillBounds
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
                    val credentials = mapOf("username" to username, "password" to password)
                    api.login(credentials).enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT)
                                    .show()
                                navCon.navigate("splash") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG)
                                .show()
                        }
                    })
                },
                modifier = Modifier.fillMaxWidth(),
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
    LoginScreen(
        navCon = NavController(LocalContext.current),
    )
}
