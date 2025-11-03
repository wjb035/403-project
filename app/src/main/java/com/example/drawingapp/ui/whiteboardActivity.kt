package com.example.drawingapp.ui

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.applyCanvas
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drawingapp.R
import com.example.drawingapp.ui.whiteboardtheme.Typography
import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.util.Log
import com.example.drawingapp.network.RetrofitInstance
import com.example.drawingapp.network.PromptApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import kotlinx.coroutines.launch
import java.io.OutputStream


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "drawData")

@Composable
fun whiteboard(navCon: NavController) {
    WhiteboardSimTheme {
        Image(
            painter = painterResource(id = R.drawable.white_layer),
            contentDescription = "background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        val context = LocalContext.current.applicationContext
        val coroutineScope = rememberCoroutineScope()

        // Variables that we use later on, including the current pen color, the list of lines (on the canvas),
        // the size of the brush, whether we're erasing or not, and whether we can draw or not.
        var currentColor by remember { mutableStateOf(Color.Black) }
        val lines = remember { mutableStateListOf<Line>() }
        var brushSize by remember { mutableFloatStateOf(10f) }
        var isEraser by remember { mutableStateOf(false) }
        var canDraw by remember { mutableStateOf(false) }
        var remainingTime by remember { mutableStateOf(10000L) }
        var countdown by remember { mutableStateOf("Press \"Ready\" when you're ready to draw!") }
        var userHasStarted by remember { mutableStateOf(false) }
        var showButton by remember { mutableStateOf(false) }
        val drawData = booleanPreferencesKey("drawData")
        var displayReady by remember { mutableStateOf(true) }
        var uriDay by remember { mutableStateOf<Uri?>(null) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (!granted) {
                Toast.makeText(
                    context,
                    "This app requires permission for something",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
        // PROMPT LOAD HERE
        var prompt by remember { mutableStateOf("Loading daily prompt...") }
        LaunchedEffect(Unit) {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val savedPrompt = prefs.getString("daily_prompt", null)
            prompt = savedPrompt ?: "No daily prompt available yet."
        }
    }
    // PROMPT LOAD HERE
    var prompt by remember {mutableStateOf("Loading daily prompt...")}
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.promptApi.getTodaysPrompt()
            prompt = response.text
        } catch (e: Exception) {
            prompt = "Failed to load prompt."
            Log.e("Whiteboard", "Error fetching prompt", e)
        }
    }


        LaunchedEffect(userHasStarted) {
            if (userHasStarted) {
                if (remainingTime > 0) {
                    object : CountDownTimer(remainingTime, 1000) {

                        override fun onTick(millisUntilFinished: Long) {
                            remainingTime = millisUntilFinished
                            val seconds = millisUntilFinished / 1000
                            countdown = String.format("%02d", seconds % 60)

                        }

                        override fun onFinish() {
                            countdown = "Time's up!"
                            canDraw = false
                            coroutineScope.launch {
                                incrementCounter(context, drawData, false)
                            }
                            showButton = true
                        }
                    }.start()
                }
            }
        }

        LaunchedEffect(Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        LaunchedEffect(Unit) {
            displayReady = getHasDrawn(context, drawData)
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navCon.navigate("home") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("To Home Screen")
                }

                AnimatedVisibility(visible = displayReady) {
                    Button(
                        onClick = {
                            if (!userHasStarted) {
                                userHasStarted = true
                                canDraw = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Ready")
                    }
                }
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = prompt,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = countdown,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                AnimatedVisibility(visible = showButton) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                uriDay = saveDrawing(context, lines, false, prompt)
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_STREAM, uriDay)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    type = "image/png"
                                }
                                context.startActivity(sendIntent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Share")
                    }
                }

                Canvas(
                    modifier = Modifier
                        .size(width = 360.dp, height = 360.dp)
                        .background(Color.White)
                        .border(6.dp, color = MaterialTheme.colorScheme.primary)
                        .pointerInput(true) {
                            val drawFlow: Flow<Boolean> = context.dataStore.data
                                .map { settings ->
                                    settings[drawData] ?: true
                                }
                            val isDailyDrawing = drawFlow.first()
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val startReal = change.position - dragAmount
                                val endReal = change.position
                                val startOBS =
                                    startReal.x < 0f || startReal.x > 1000f || startReal.y < 0f || startReal.y > 1000f
                                val endOBS =
                                    endReal.x < 0f || endReal.x > 1000f || endReal.y < 0f || endReal.y > 1000f

                                if (!endOBS && !startOBS && canDraw && isDailyDrawing) {
                                    val line = Line(
                                        start = change.position - dragAmount,
                                        end = change.position,
                                        color = if (isEraser) Color.White else currentColor,
                                        strokeWidth = brushSize
                                    )
                                    lines.add(line)
                                }
                            }
                        }) {
                    lines.forEach { line ->
                        drawLine(
                            color = line.color,
                            start = line.start,
                            end = line.end,
                            strokeWidth = line.strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    selectColor { selectedColor ->
                        currentColor = selectedColor
                        isEraser = false
                    }
                    Button(
                        onClick = { isEraser = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Eraser")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrushSizeSelector(
                        brushSize,
                        onSizeSelected = { selectedSize -> brushSize = selectedSize },
                        isEraser = isEraser,
                        keepMode = { keepEraserMode -> isEraser = keepEraserMode })

                    Button(
                        onClick = { lines.clear() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("Reset Canvas")
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                saveDrawing(context, lines, true, prompt)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save...", fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                incrementCounter(context, drawData, true)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("(Debug) Reset?")
                    }
                }
            }
        }
    }
}

suspend fun getHasDrawn(context: Context, drawData: Preferences.Key<Boolean>): Boolean {
    val drawFlow: Flow<Boolean> = context.dataStore.data
        .map { settings ->
            settings[drawData] ?: true
        }
    return drawFlow.first()
}

suspend fun incrementCounter(context: Context, drawData: Preferences.Key<Boolean>, result: Boolean) {
    context.dataStore.edit { settings ->
        settings[drawData] = result
    }
}

@Composable
fun selectColor(onColorSelected: (Color) -> Unit) {
    val context = LocalContext.current.applicationContext
    val colorMap = mapOf(
        Color.Red to "Red",
        Color.Yellow to "Yellow",
        Color.Green to "Green",
        Color.Blue to "Blue",
        Color.Cyan to "Cyan",
        Color.Magenta to "Magenta",
        Color.Black to "Black"
    )
    Row {
        colorMap.forEach { (color, name) ->
            Box(
                Modifier
                    .size(36.dp)
                    .background(color, CircleShape)
                    .padding(10.dp)
                    .clickable {
                        onColorSelected(color)
                        Toast
                            .makeText(context, name, Toast.LENGTH_SHORT)
                            .show()
                    }
            )

        }
    }
}

@Composable
fun BrushSizeSelector(currentSize: Float, onSizeSelected: (Float) -> Unit, isEraser: Boolean, keepMode: (Boolean) -> Unit) {
    var sizeText by remember { mutableStateOf(currentSize.toString()) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        BasicTextField(
            value = sizeText,
            onValueChange = {
                sizeText = it
                val newSize = it.toFloatOrNull() ?: currentSize
                onSizeSelected(newSize)
                keepMode(isEraser)
            },
            textStyle = TextStyle(fontSize = 20.sp, color = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .width(60.dp)
                .background(color = MaterialTheme.colorScheme.onPrimary, CircleShape)
                .padding(10.dp)
        )
        Text(
            text = "px",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

data class Line(val start: Offset, val end: Offset, val color: Color, val strokeWidth: Float = 10f)

suspend fun saveDrawing(context: Context, lines: List<Line>, downloadToDevice: Boolean, prompt: String): Uri? {
    val bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
    bitmap.applyCanvas {
        drawColor(android.graphics.Color.WHITE)
        lines.forEach { line ->
            val paint = Paint().apply {
                color = line.color.toArgb()
                strokeWidth = line.strokeWidth
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
            }
            drawLine(line.start.x, line.start.y, line.end.x, line.end.y, paint)

        }
    }
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "quickdraw-${prompt}")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        if (downloadToDevice) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/WhiteboardSim")
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (uri != null) {
        val outputStream: OutputStream? = resolver.openOutputStream(uri)
        outputStream.use {
            if (it != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            if (downloadToDevice) {
                Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
            }
        }

    } else {
        Toast.makeText(context, "Unable to save, please try again.", Toast.LENGTH_SHORT).show()
    }
    return uri
}

@Preview
@Composable
fun whiteboardPreview() {
    WhiteboardSimTheme {
        whiteboard(rememberNavController())
    }
}
