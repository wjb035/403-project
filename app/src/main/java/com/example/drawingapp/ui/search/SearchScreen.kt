package com.example.drawingapp.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drawingapp.model.User
import com.example.drawingapp.network.RetrofitInstance
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage

@Composable
fun SearchScreen(navCon: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var allUsers by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Load all the users from the database
    LaunchedEffect(Unit) {
        isLoading = true
        scope.launch {
            try {
                allUsers = RetrofitInstance.userApi.getAllUsers()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load users", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }

        }
    }

    // Filtered list based on search query
    val filteredUsers = if (searchQuery.isEmpty()) allUsers else {
        allUsers.filter { it.username.contains(searchQuery, ignoreCase = true) }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // List of results, if loading show a load screen
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(filteredUsers) { user ->
                        Text(
                            text = user.username,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
        // popup for the mini profile
        selectedUser?.let { user ->
            MiniProfilePopup(
                user = user,
                onDismiss = { selectedUser = null },
                onViewProfile = { navCon.navigate("profile") }
            )
        }
    }
}

// Function for the mini profile popup
@Composable
fun MiniProfilePopup(
    user: User,
    onDismiss: () -> Unit,
    onViewProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card (
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(0.85f)
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                AsyncImage(
                    model = user.profilePicture,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                )

                Text(text = user.username, style = MaterialTheme.typography.titleMedium)
                Text(text = user.bio ?: "No bio available", color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onViewProfile) {
                    Text("View Full Profile")
                }

                TextButton(onClick = onDismiss) {
                    Text("Close", color = Color.Red)
                }
            }
        }
    }
}

