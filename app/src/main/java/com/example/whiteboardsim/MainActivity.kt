package com.example.whiteboardsim

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.applyCanvas

import com.example.whiteboardsim.ui.theme.WhiteboardSimTheme
import java.io.OutputStream

import androidx.activity.result.launch
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WhiteboardSimTheme {
                    // Creates a navController for switching between views. You start
                    // off in the home menu.
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(4.dp)
                    ){
                        // All the different routes. Each one is given a reference
                        // to the nav controller so you can navigate between areas.
                        composable(route = "home"){
                            home(navCon=navController)
                        }
                        composable(route="whiteboard"){
                            whiteboard(navCon=navController)
                        }
                    }

                }
            }
        }
    }

@Composable
fun home(navCon: NavController){
    // Beneath all the flavor text (modifier things), it's just a simple white screen
    // with a button to go to the whiteboard. The idea is that this will be updated with more and more-
    // this is just so we can navigate for now.
    Box(modifier=Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxWidth()
            .offset(x = 0.dp, y = 850.dp)){
            Row(
                Modifier.fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){

                Button(onClick = {
                    navCon.navigate("whiteboard")
                }) {
                    Text("Whiteboard")
                }

            }
        }
    }

}
@Composable
fun whiteboard(navCon: NavController){
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()

    // Variables that we use later on, including the current pen color, the list of lines (on the canvas),
    // the size of the brush, whether we're erasing or not, and whether we can draw or not.
    var currentColor by remember {mutableStateOf(Color.Black)}
    val lines = remember{ mutableStateListOf<Line>()}
    var brushSize by remember {mutableFloatStateOf(10f)}
    var isEraser by remember {mutableStateOf(false)}
    var canDraw by remember {mutableStateOf(true)}
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {granted ->
        if (!granted) {
            Toast.makeText(context, "This app requires permission for something", Toast.LENGTH_SHORT).show()

        }
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }




    // A button within a box (for tidy placement). This button goes back to the home screen using the passed
    // in navController.
    Box(modifier=Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxWidth()
            .offset(x = 25.dp, y = 50.dp)){
            Row(
                Modifier.fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){

                Button(onClick = {
                    navCon.navigate("home")
                }) {
                    Text("home")
                }

            }
        }
    }

    // Box containing a button that I'm using for debug purposes.
    // Further along, the user will not be able to draw once the timer is up.
    // I'm just testing with it now- this has no real purpose yet.
    Box(modifier=Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxWidth()
            .offset(x = 150.dp, y = 700.dp)){
            Row(
                Modifier.fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Button(onClick = {
                    if (canDraw){
                        canDraw = false
                    }
                    else{
                        canDraw = true
                    }
                }) {
                    Text("Lock")
                }
            }
        }
    }

    // Box containing the widget for all the selectable colors, as well as whether you can erase or not.
    // Clicking a color button will change your pen color to that color, and clicking the erase button will
    // swap to erase mode.
    Box(modifier=Modifier.fillMaxSize()){
        Column(modifier=Modifier
            .fillMaxWidth()
            .offset(x=100.dp,y=800.dp)){
            Row(Modifier.fillMaxWidth()
                .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                selectColor { selectedColor ->
                    currentColor = selectedColor
                    isEraser = false
                }
                Button(onClick = { isEraser = true }) {
                    Text("Eraser")
                }
            }
        }
    }
    // Box that contains the pen size changer, the reset canvas button, and the save button.
    Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .align(Alignment.BottomCenter)) {
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

            Button(onClick = {
                coroutineScope.launch {
                    saveDrawing(context, lines)
                }
            }) {
                Text("Save")
            }
        }
    }
        // The star of the show- the canvas.
        Box(modifier = Modifier
            .fillMaxSize()
            .offset(5.dp,200.dp)) {
            Canvas(
                modifier = Modifier.size(width = 400.dp, height = 400.dp)
                    .background(Color.Red)
                    .pointerInput(true) {

                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            // for a line to be considered within bounds it must start and end in the bounds
                            // There was a weird issue where lines were being cut of incorrectly, and it had to do
                            // with the fact I wasn't checking if the line started in the canvas. That is why I check if
                            // all lines start and end in the canvas. Otherwise: it's not applied.
                            val startReal = change.position-dragAmount
                            val endReal = change.position
                            val startOBS = startReal.x < 0f || startReal.x > 1000f || startReal.y < 0f || startReal.y > 1000f
                            val endOBS = endReal.x < 0f || endReal.x > 1000f || endReal.y < 0f || endReal.y > 1000f

                            // If the user is within bounds, and is able to draw (timer hasn't expired,
                            // the line is applied.
                            if (!endOBS && !startOBS && canDraw) {
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


@Composable
fun selectColor(onColorSelected: (Color) -> Unit){
    val context = LocalContext.current.applicationContext
    // Selectable color function that maps different colors to strings of their names, which shows the
    // user what they switched to.
    val colorMap = mapOf(Color.Red to "Red",
        Color.Yellow to "Yellow",
        Color.Green to "Green",
        Color.Blue to "Blue",
        Color.Cyan to "Cyan",
        Color.Magenta to "Magenta",
        Color.Black to "Black")
    Row{
        colorMap.forEach {(color,name) ->
            Box(Modifier.size(25.dp)
                .background(color,CircleShape)
                .padding(10.dp)
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
suspend fun saveDrawing(context: Context, lines: List<Line>){
    val bitmap = Bitmap.createBitmap(1000,1000,Bitmap.Config.ARGB_8888)
    bitmap.applyCanvas {
        drawColor(android.graphics.Color.WHITE)
        lines.forEach { line ->
            val paint = android.graphics.Paint().apply {
                color = line.color.toArgb()
                strokeWidth = line.strokeWidth
                style = android.graphics.Paint.Style.STROKE
                strokeCap = android.graphics.Paint.Cap.ROUND
                strokeJoin = android.graphics.Paint.Join.ROUND
            }
            drawLine(line.start.x, line.start.y, line.end.x, line.end.y, paint)

        }
    }
    val contentValues = ContentValues().apply{
        put(MediaStore.MediaColumns.DISPLAY_NAME, "drawing_${System.currentTimeMillis()}")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH,"Pictures/WhiteboardSim")
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (uri != null){
        val outputStream: OutputStream? = resolver.openOutputStream(uri)
        outputStream.use {
            if (it != null){
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
        }

    }
    else{
        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
    }
}


