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
class RegisterActivity : AppCompatActivity() {
    // Declare UI components
    var etUsername: EditText? = null
    var etPassword: EditText? = null
    var etConfirmPassword: EditText? = null // Input fields
    var btnRegister: Button? = null // Register button
    var tvGoToLogin: TextView? = null // Link to go back to login screen

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // connect this activity to its layout file (res/layout/activity_register.xml)
        setContentView(R.layout.activity_register)

        // initialize UI components
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        // Create Retrofit API client
        val api: ApiService = RetrofitClient.getClient().create<ApiService>(ApiService::class.java)

        // When the user taps the "Register" button:
        btnRegister.setOnClickListener({ v ->
            // Read user input
            val username = etUsername.getText().toString()
            val password = etPassword.getText().toString()
            val confirm = etConfirmPassword.getText().toString()

            // Basic validation checks
            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this@RegisterActivity, "Passwords do not match", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            // Create request body for the API
            // Map<String, String> userData = new HashMap<>();
            // userData.put("username", username);
            // userData.put("password", password);


            // Send POST request to /api/users/register
            val user = User()
            user.username = username
            user.password = password
            api.register(user).enqueue(object : Callback<User?> {
                override fun onResponse(
                    @NonNull call: Call<User?>,
                    @NonNull response: Response<User?>
                ) {
                    if (response.isSuccessful()) {
                        // Registration successful - notify user
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Redirect back to LoginActivity
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish() // Close this screen so the user can't go back with Back button
                    } else {
                        // API returned an error (e.g. username already taken)
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(@NonNull call: Call<User?>, @NonNull t: Throwable) {
                    // Connection/network failure
                    Builder(this@RegisterActivity)
                        .setTitle("Registration Error") // Dialog title
                        .setMessage("Error: " + t.message) // Dialog message
                        .setPositiveButton("OK", null) // OK button to dismiss
                        .show()
                }
            })
        })

        // When user taps "Already have an account? Log in"
        tvGoToLogin.setOnClickListener({ v ->
            val intent: Intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}

