package com.example.drawingapp.loginscreen;

// Android imports

import android.widget.Toast;
import androidx.compose.foundation.layout.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.LocalContext;
import androidx.compose.ui.text.input.PasswordVisualTransformation;
import androidx.compose.ui.unit.dp;
import androidx.navigation.NavController;
import com.example.drawingapp.loginscreen.model.User;
import com.example.drawingapp.loginscreen.network.ApiService;
import com.example.drawingapp.loginscreen.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LoginActivity
 * This screen allows the user to enter a username and password,
 * sends those credentials to spring boot API thru Retrofit,
 * handles success/failure response.
 */

public class LoginActivity extends AppCompatActivity {

    // declare UI components
    EditText etUsername, etPassword;    // input fields
    Button btnLogin;                    // login button
    TextView tvGoToRegister;            // link to Register screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the layout file for the current screen (res/layout/activity_login.xml)
        setContentView(R.layout.activity_login);

        // connect ui elements from XML to Java variables
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Create a retrofit API client from RetrofitClient class
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        // When user taps the login button
        btnLogin.setOnClickListener(v -> {
            // Read user input from text fields
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            // prepare the request body (Backend server expects json object)
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", username);
            credentials.put("password", password);

            // send POST request to /api/users/login
            api.login(credentials).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    // If the server responds successfully (HTTP 200)
                    if (response.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    }
                    // If credentials are wrong or server returns 400-401
                    else {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    // Connection/network failure
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Login Error")          // Dialog title
                            .setMessage("Error: " + t.getMessage())  // Dialog message
                            .setPositiveButton("OK", null)           // OK button to dismiss
                            .show();
                }
            });
        });

        // When user clicks "No account? Register" text:
        tvGoToRegister.setOnClickListener(v -> {
            // Switch to RegisterActivity screen
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}