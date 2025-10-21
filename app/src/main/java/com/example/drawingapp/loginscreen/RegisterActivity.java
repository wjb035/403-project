package com.example.drawingapp.loginscreen;

// Android imports

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.basketball.logintestapplication.R;
import com.basketball.logintestapplication.model.User;
import com.basketball.logintestapplication.network.ApiService;
import com.basketball.logintestapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * RegisterActivity
 * ----------------
 * This activity allows a new user to create an account by sending
 * a POST request with username and password to your Spring Boot API.
 */

public class RegisterActivity extends AppCompatActivity {

    // Declare UI components
    EditText etUsername, etPassword, etConfirmPassword; // Input fields
    Button btnRegister;                                 // Register button
    TextView tvGoToLogin;                               // Link to go back to login screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // connect this activity to its layout file (res/layout/activity_register.xml)
        setContentView(R.layout.activity_register);

        // initialize UI components
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Create Retrofit API client
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        // When the user taps the "Register" button:
        btnRegister.setOnClickListener(v -> {
            // Read user input
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String confirm = etConfirmPassword.getText().toString();

            // Basic validation checks
            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create request body for the API
            // Map<String, String> userData = new HashMap<>();
            // userData.put("username", username);
            // userData.put("password", password);


            // Send POST request to /api/users/register
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            api.register(user).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful()) {
                        // Registration successful - notify user
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                        // Redirect back to LoginActivity
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish(); // Close this screen so the user can't go back with Back button
                    } else {
                        // API returned an error (e.g. username already taken)
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    // Connection/network failure
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("Registration Error")          // Dialog title
                            .setMessage("Error: " + t.getMessage())  // Dialog message
                            .setPositiveButton("OK", null)           // OK button to dismiss
                            .show();
                }
            });
        });

        // When user taps "Already have an account? Log in"
        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

