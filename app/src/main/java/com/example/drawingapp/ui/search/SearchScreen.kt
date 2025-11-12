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
import androidx.compose.ui.platform.LocalContext

@Composable
fun SearchScreen(navCon: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var allUsers by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

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

    // Sample data
    val items = listOf(
        "User1", "User2", "User3", "User4", "Henri",
        "Alex", "Keahi", "Leo", "Prather"
    )

    // Filtered list based on search query
    val filteredUsers = if (searchQuery.isEmpty()) allUsers else {
        allUsers.filter { it.username.contains(searchQuery, ignoreCase = true) }
    }

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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
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
}
