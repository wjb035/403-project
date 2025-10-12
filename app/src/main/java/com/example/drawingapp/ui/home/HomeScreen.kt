package com.example.drawingapp.ui.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun HomeScreen(navCon: NavController) {
    val context = LocalContext.current
    Column(modifier = Modifier.
        fillMaxSize().
        padding(16.dp)) {
        Text("Home Screen")

        // Buttons to navigate to prompt and whiteboard
        Button(onClick = {
            navCon.navigate("prompt")
        }) {
            Text("Go to Prompt")
        }

        Button(onClick = {
            navCon.navigate("whiteboard")
        }) {
            Text("Go to Whiteboard")
        }
        Button(onClick = {
            navCon.navigate("leaderboard")
        }) {
            Text("Go to Gallery")
        }
    }




}