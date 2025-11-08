package com.example.drawingapp.ui

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.health.connect.datatypes.Device
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.applyCanvas
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.io.OutputStream
import kotlin.apply
import kotlin.collections.forEach
import kotlin.io.use
import kotlin.text.toFloatOrNull
import kotlin.to
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.drawingapp.R
import com.example.drawingapp.network.RetrofitInstance
import com.example.drawingapp.model.UserViewModel
import com.example.drawingapp.network.PromptApi
import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "drawData")

@Composable
fun whiteboard(navCon: NavController, userViewModel: UserViewModel) {
    val currentUser = userViewModel.currentUser
    WhiteboardSimTheme {
    val context = LocalContext.current
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
    var getUriFromCanvas by remember { mutableStateOf(false) }
    /*val drawFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[drawData] ?: true
        }
*/
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
        var promptId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.promptApi.getTodaysPrompt()
            prompt = response.text
            promptId = response.id
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

    // I don't think this is necessary? But I'm afraid to get rid of it.
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    LaunchedEffect(Unit) {
        displayReady = getHasDrawn(context, drawData)
    }
    //LaunchedEffect(getUriFromCanvas){
    //uriDay= saveDrawing(context,lines, true)
    //}
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.white_layer),
                contentDescription = "background",
                //contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
                //contentScale = ContentScale.FillBounds

            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Row(
                Modifier.fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                AnimatedVisibility(
                    visible = showButton,

                    ) {
                    Button(onClick = {
                        coroutineScope.launch {
                            uriDay = saveDrawing(context, lines, false, prompt)


                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                //putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                                putExtra(Intent.EXTRA_STREAM, uriDay)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                type = "image/png"
                                //type = "text/plain"
                            }


                            context.startActivity(sendIntent)
                        }

                    }) {
                        Text("Share")
                    }

                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = 0.dp, y = 80.dp)
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prompt,
                    modifier = Modifier,
                    fontSize = 22.sp
                )

            }
        }
    }



        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .align(Alignment.TopCenter)
                    .offset(x = 0.dp, y = 40.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(onClick = {
                        navCon.navigate("home")
                    }) {
                        Text("home")
                    }
                    AnimatedVisibility(

                        visible = displayReady

                    ) {
                        Button(onClick = {
                            if (!userHasStarted) {
                                userHasStarted = true
                                canDraw = true
                            }
                        }) {
                            Text("Ready?")
                        }
                    }
                    Button(onClick = {
                        coroutineScope.launch {
                            incrementCounter(context, drawData, true)

                        }
                    }) {
                        Text("Reset?")
                    }


                }
            }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = 0.dp, y = 110.dp)
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = countdown,
                    modifier = Modifier,
                    fontSize = 18.sp
                )

            }
        }
    }


    // Box containing the widget for all the selectable colors, as well as whether you can erase or not.
    // Clicking a color button will change your pen color to that color, and clicking the erase button will
    // swap to erase mode.
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)

                .offset(x = 0.dp, y = -50.dp)
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                selectColor { selectedColor ->
                    currentColor = selectedColor
                    isEraser = false
                }
                //Button(onClick = { isEraser = true }) {
                    //Text("Eraser")
               // }
            }
        }
    }
    // Box that contains the pen size changer, the reset canvas button, and the save button.
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .align(Alignment.BottomCenter)

        ) {
            Row(
                Modifier.fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                BrushSizeSelector(
                    brushSize, onSizeSelected = { selectedSize -> brushSize = selectedSize },
                    isEraser = isEraser, keepMode = { keepEraserMode -> isEraser = keepEraserMode })

                Button(onClick = { lines.clear() }) {
                    Text("Reset")
                }
                Button(onClick = { isEraser = true }) {
                    Text("Eraser")
                }
                Button(enabled = currentUser?.id != null, onClick = {
                    coroutineScope.launch {
                        // SAVE TO DEVICE FIRST
                        val localUri = saveDrawing(context, lines, true, prompt)
                        if (localUri != null) {
                            try {
                                // UPLOAD TO BACKEND which uploads to firebase!
                                // Read the saved file
                                val inputStream = context.contentResolver.openInputStream(localUri)
                                val tempFile = File(context.cacheDir, "upload.png")

                                // Copy URI data into temp file
                                inputStream?.use { input ->
                                    tempFile.outputStream().use { output ->
                                        input.copyTo(output)
                                    }
                                }

                                // Convert File to MultipartBody.Part
                                val requestFile = tempFile.asRequestBody("image/png".toMediaTypeOrNull())
                                val multipartBody = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

                                // Get user ID
                                val userIdRequest = currentUser!!.id!!.toString().toRequestBody("text/plain".toMediaType())

                                // Get prompt ID
                                val promptIdRequest = promptId.toString().toRequestBody("text/plain".toMediaType())
                                // Upload with retrofit
                                val response = RetrofitInstance.drawingApi.uploadDrawing(multipartBody, userIdRequest, promptIdRequest)
                                Toast.makeText(context, "Uploaded to Firebase!", Toast.LENGTH_SHORT).show()

                            } catch (e: Exception) {
                                Log.e("Upload", "Failed uploading drawing", e)
                                Toast.makeText(
                                    context,
                                    "Failed to upload drawing",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }) {
                    Text("Save")
                }
            }
        }


        // The star of the show- the canvas.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(0.dp, 0.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                //modifier = Modifier.align(Center)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(
                        modifier = Modifier.size(width = 400.dp, height = 400.dp)
                            .background(Color.White)
                            .border(6.dp, Color.Black)
                            .pointerInput(true) {
                                val drawFlow: Flow<Boolean> = context.dataStore.data
                                    .map { settings ->
                                        // No type safety.
                                        settings[drawData] ?: true
                                    }
                                val isDailyDrawing = drawFlow.first()
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    // for a line to be considered within bounds it must start and end in the bounds
                                    // There was a weird issue where lines were being cut of incorrectly, and it had to do
                                    // with the fact I wasn't checking if the line started in the canvas. That is why I check if
                                    // all lines start and end in the canvas. Otherwise: it's not applied.
                                    val startReal = change.position - dragAmount
                                    val endReal = change.position
                                    val startOBS =
                                        startReal.x < 0f || startReal.x > 1000f || startReal.y < 0f || startReal.y > 1000f
                                    val endOBS =
                                        endReal.x < 0f || endReal.x > 1000f || endReal.y < 0f || endReal.y > 1000f

                                    // If the user is within bounds, and is able to draw (timer hasn't expired,
                                    // the line is applied.

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
            }
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

suspend fun incrementCounter(context: Context,drawData: Preferences.Key<Boolean>, result: Boolean) {
    context.dataStore.edit { settings ->
        settings[drawData] = result
    }
}

@Composable
fun selectColor(onColorSelected: (Color) -> Unit){
    val context = LocalContext.current.applicationContext
    // Selectable color function that maps different colors to strings of their names, which shows the
    // user what they switched to.
    val colorMap = mapOf(
        Color.Red to "Red",
        Color.Yellow to "Yellow",
        Color.Green to "Green",
        Color.Blue to "Blue",
        Color.Cyan to "Cyan",
        Color.Magenta to "Magenta",
        Color.Black to "Black"
    )
    Row{
        colorMap.forEach { (color,name) ->
            Box(Modifier.size(25.dp)
                .background(color,CircleShape)
                .padding(20.dp)
                .clickable{
                    onColorSelected(color)
                    Toast.makeText(context,name,Toast.LENGTH_SHORT).show()
                }
            )

        }
    }
}

@Composable
fun BrushSizeSelector(currentSize: Float, onSizeSelected: (Float) -> Unit, isEraser: Boolean, keepMode: (Boolean) -> Unit){
    var sizeText by remember { mutableStateOf(currentSize.toString())}

    Row{
        BasicTextField(
            value=sizeText,
            onValueChange = {
                sizeText = it
                val newSize = it.toFloatOrNull() ?: currentSize
                onSizeSelected(newSize)
                keepMode(isEraser)
            },
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier.width(60.dp)
                .background(Color.LightGray, CircleShape)
                .padding(8.dp)
        )
        Text(" px", Modifier.align(Alignment.CenterVertically))
    }
}

data class Line(val start: Offset, val end: Offset, val color: Color, val strokeWidth: Float = 10f)

// All this fun stuff is for saving the drawing as an image. The code from this came from this video:
// https://youtu.be/nMeO3XxjfBs?si=8Pkxk9B5QIwBqa8d
suspend fun saveDrawing(context: Context, lines: List<Line>,downloadToDevice: Boolean, prompt: String): Uri? {
    val bitmap = Bitmap.createBitmap(1000,1000,Bitmap.Config.ARGB_8888)
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

    if (uri != null){
        val outputStream: OutputStream? = resolver.openOutputStream(uri)
        outputStream.use {
            if (it != null){
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            if (downloadToDevice) {
                Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
            }
        }

    }
    else{
        Toast.makeText(context, "Unable to save, please try again.", Toast.LENGTH_SHORT).show()
    }
    return uri
}

@Preview
@Composable
fun whiteboardPreview(){
    val userViewModel = remember { UserViewModel() }
    whiteboard(rememberNavController(), userViewModel)
}


