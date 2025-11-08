package com.example.drawingapp.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drawingapp.R
import com.example.drawingapp.network.RetrofitInstance
import com.example.drawingapp.ui.dataStore
import com.example.drawingapp.ui.getHasDrawn
import com.example.drawingapp.ui.incrementCounter
import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "drawData")
@Composable
fun HomeScreen(navCon: NavController) {
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()
    val drawData = booleanPreferencesKey("drawData")
    var canDraw by remember { mutableStateOf(false) }
    var checked by remember {mutableStateOf(true)}
    var hasDrawn by remember {mutableStateOf(false)}
    LaunchedEffect(checked){
        //incrementCounter(context, drawData, false)
        canDraw = RetrofitInstance.CanDrawApi.getTime()
        checked = false
    }
    LaunchedEffect(Unit) {
        hasDrawn = HasDrawn(context, drawData)
    }

    WhiteboardSimTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.white_layer),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Button(onClick = {
                if (canDraw && !hasDrawn) {
                    navCon.navigate("whiteboard")
                }
                else {
                    checked = true
                    Toast.makeText(
                        context,
                        "It's not your turn to draw!",
                        Toast.LENGTH_SHORT
                    ).show()
                }



            }) {
                Text("Draw")
            }
        }
    }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x=150.dp, y=0.dp),
            contentAlignment = Alignment.Center
        ) {


            Button(onClick = {
                    coroutineScope.launch {
                        changeVal(context, drawData, false)
                        hasDrawn = HasDrawn(context, drawData)
                    }
                }



            ) {
                Text("Reset")
            }
        }

}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(navCon = rememberNavController())
}
suspend fun HasDrawn(context: Context,drawData: Preferences.Key<Boolean>): Boolean{
    val drawFlow: Flow<Boolean> = context.dataStore.data
        .map { settings ->
            // No type safety.
            settings[drawData] ?: true
        }
    return drawFlow.first()
}

suspend fun changeVal(context: Context,drawData: Preferences.Key<Boolean>, result: Boolean) {
    context.dataStore.edit { settings ->
        settings[drawData] = result
    }
}