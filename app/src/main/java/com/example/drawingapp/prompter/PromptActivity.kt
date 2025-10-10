package com.example.drawingapp.prompter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PromptScreen(navCon: NavController) {

    // State for the prompt text
    var promptText by remember { mutableStateOf("Loading your drawing prompt...") }

    // Launch coroutine to fetch prompt in background
    LaunchedEffect(Unit){
        try {
            val wordFetcher = WordFetch(WordNetProvider.dictionary) // <-- use shared dictionary
            val prompt = withContext(Dispatchers.Default) {
                wordFetcher.getDrawingPrompt()
            }
            promptText = prompt
        } catch (e: Exception) {
            promptText = "Did not load prompt: ${e.localizedMessage}"
        }
    }
    // UI STUFF
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = promptText,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            navCon.popBackStack() // <-- acts like the back button
        }) {
            Text("Back")
        }
    }

}
