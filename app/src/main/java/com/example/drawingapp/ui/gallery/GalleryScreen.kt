package com.example.drawingapp.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GalleryScreen(navCon: NavController) {

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Gallery Screen")
        Spacer(modifier = Modifier.height(8.dp))
        // ADD GALLERY STUFF HERE
    }
}