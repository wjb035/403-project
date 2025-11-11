package com.example.drawingapp.ui.home

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drawingapp.R
import com.example.drawingapp.ui.dataStore
import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "drawData")
@Composable
fun HomeScreen(navCon: NavController) {
    val context = LocalContext.current
    val drawData = booleanPreferencesKey("drawData")
    var soundPool: SoundPool? by remember { mutableStateOf(null) }
    var soundId: Int? by remember { mutableStateOf(null) }
    var hasDrawn by remember {mutableStateOf(true)}
    LaunchedEffect(context) {
        val attributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(attributes)
            .build()
        soundId = soundPool?.load(context, R.raw.deck_ui_default_activation, 1)
    }
    /*
    *
    LaunchedEffect(checked){
        //incrementCounter(context, drawData, false)
        canDraw = RetrofitInstance.CanDrawApi.getTime()
        checked = false
    }
    *
    *

    *
    LaunchedEffect(Unit) {
        hasDrawn = HasDrawn(context, drawData)
    }
    * */
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
                soundId?.let { soundPool?.play(it, 1f, 1f, 0, 0, 1f) }
                //if (canDraw && !hasDrawn) {
                navCon.navigate("whiteboard")
                //}

                /*
                *
                 else {
                    checked = true
                    Toast.makeText(
                        context,
                        "It's not your turn to draw!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                *
                * */
            }) {
                Text("Draw")
            }
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
