package com.example.drawingapp.loginscreen

// Android imports
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
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
import com.example.drawingapp.loginscreen.model.User
import com.example.drawingapp.loginscreen.network.ApiService
import com.example.drawingapp.loginscreen.network.RetrofitClient
import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.material3.OutlinedTextField
/**
 * RegisterActivity
 * ----------------
 * This activity allows a new user to create an account by sending
 * a POST request with username and password to your Spring Boot API.
 */

@Composable
fun RegisterScreen(navCon: NavController) {
    WhiteboardSimTheme {
        // Declare UI components
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        val context = LocalContext.current

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
                "Create a Free Account!",
                style = MaterialTheme.typography.headlineSmall,
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
                                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT)
                                    .show()
                                navCon.navigate("splash") {
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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            ) {
                Text("Register")

            }
            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { navCon.navigate("login") }) {
                Text(
                    "Already have an account? Log in",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

        }
    }

}

@Preview
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navCon = NavController(LocalContext.current))
}
