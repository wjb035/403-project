package com.example.drawingapp.loginscreen

// Android imports
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drawingapp.loginscreen.model.User
import com.example.drawingapp.loginscreen.network.ApiService
import com.example.drawingapp.loginscreen.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * RegisterActivity
 * ----------------
 * This activity allows a new user to create an account by sending
 * a POST request with username and password to your Spring Boot API.
 */

@Composable
fun RegisterScreen(navCon: NavController) {
    // Declare UI components
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    val api = remember { RetrofitClient.getClient().create(ApiService::class.java) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center

    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)
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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val user = User(username = username, password = password)
                api.register(user).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Registered sucessfully", Toast.LENGTH_SHORT)
                                .show()
                            navCon.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }

                        } else {
                            Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }

                })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        Spacer(Modifier.height(12.dp))

        TextButton(onClick = { navCon.navigate("login")}) {
            Text("Already have an account? Log in")
        }

    }

}

