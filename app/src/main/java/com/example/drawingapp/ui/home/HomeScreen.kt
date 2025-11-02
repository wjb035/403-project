package com.example.drawingapp.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.drawingapp.ui.dataStore
import com.example.drawingapp.ui.getHasDrawn
import com.example.drawingapp.ui.incrementCounter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "drawData")
@Composable
fun HomeScreen(navCon: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawData = booleanPreferencesKey("drawData")
    var canGoToWhiteboard by remember{mutableStateOf(true)}

    LaunchedEffect(Unit){
        canGoToWhiteboard = getHasDrawn(context, drawData)
    }
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
            if (canGoToWhiteboard) {
                navCon.navigate("whiteboard")
            }
            else{
                Toast.makeText(context,"It's not time for you to draw yet!",Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Go to Whiteboard")
        }
    }
    Box(modifier=Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxWidth()
            .offset(x = 240.dp, y = 80.dp)){
            Row(
                Modifier.fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){

                Button(onClick = {
                    coroutineScope.launch {
                        updateVar(context, drawData, true)
                        canGoToWhiteboard = getHasDrawn(context, drawData)
                    }
                }) {
                    Text("Reset?")
                }

            }
        }
    }



}
suspend fun getHasDrawn(context: Context,drawData: Preferences.Key<Boolean>): Boolean{
    val drawFlow: Flow<Boolean> = context.dataStore.data
        .map { settings ->
            // No type safety.
            settings[drawData] ?: true
        }
    return drawFlow.first()
}
suspend fun updateVar(context: Context,drawData: Preferences.Key<Boolean>, result: Boolean) {
    context.dataStore.edit { settings ->
        settings[drawData] = result
    }
}