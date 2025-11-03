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

@Composable
fun SearchScreen(navCon: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    // Sample data
    val items = listOf(
        "User1", "User2", "User3", "User4", "Henri",
        "Alex", "Keahi", "Leo", "Prather"
    )

    // Filtered list based on search query
    val filteredItems = items.filter {
        it.contains(searchQuery, ignoreCase = true)
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

        // List of results
        LazyColumn {
            items(filteredItems) { item ->
                Text(
                    text = item,
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
