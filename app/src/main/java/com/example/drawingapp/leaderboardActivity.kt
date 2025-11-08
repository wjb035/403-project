package com.example.drawingapp

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.drawingapp.R
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlin.collections.map
import kotlin.collections.plus
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ButtonDefaults

import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.User
import com.example.drawingapp.network.DrawingApi
import com.example.drawingapp.network.RetrofitInstance
import retrofit2.Retrofit
import com.example.drawingapp.model.UserViewModel


@Composable
fun leaderboard(navCon: NavController, userViewModel: UserViewModel) {
    val currentUser = userViewModel.currentUser
    if (currentUser == null) {
        Text("Loading user...")
        return
    }


    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // DRAWING STORAGE SETUP
    var drawings by remember { mutableStateOf<List<Drawing>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedDrawing by remember { mutableStateOf<Drawing?>(null) }

    // Tabs
    var selectedTab by remember { mutableStateOf("Popular") }
    val tabs =  listOf("Popular", "New", "Following")

    // Load drwaings based on the tab thats selected
    fun loadDrawings(){
        scope.launch {
            isLoading = true
            try {
                drawings = when (selectedTab) {
                    "Popular" -> RetrofitInstance.drawingApi.getLeaderboardByLikes()
                    "New" -> RetrofitInstance.drawingApi.getLeaderboardByNew()
                    "Following" -> RetrofitInstance.drawingApi.getLeaderboardByLikes()
                    else -> emptyList()
                }
                // Following tab filter
                if (selectedTab == "Following") {
                    val followingIds = currentUser.following.map { it.id }
                    drawings = drawings.filter { it.user.id in followingIds }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Couldn't load drawings", Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }

    // Fetch drawings for the first time
    LaunchedEffect(selectedTab) {
        loadDrawings()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Tabs Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                Button(

                    onClick = { selectedTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Text(tab)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading indicator
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(drawings) { drawing ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        onClick = {
                            selectedDrawing = drawing
                            showDialog = true
                        }
                    ) {
                        AsyncImage(
                            model = drawing.imageUrl,
                            contentDescription = "Drawing Image",
                            modifier = Modifier.fillMaxWidth().height(250.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("By: ${drawing.user.username}", fontSize = 16.sp)
                            Text("Likes: ${drawing.likesCount}", fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // Detailed drawing view
        if (showDialog && selectedDrawing != null) {
            DrawingDialog(
                drawing = selectedDrawing!!,
                onDismiss = { showDialog = false },
                onLike = { drawingId ->
                    scope.launch {
                        try {
                            val updated = RetrofitInstance.drawingApi.likeDrawing(drawingId, currentUser.id!!)
                            drawings = drawings.map { if (it.id == drawingId) updated else it }
                            selectedDrawing = updated
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to like drawing", Toast.LENGTH_SHORT).show()
                        }
                    }
                }, onUnlike = { drawingId ->
                    scope.launch {
                        try {
                            val updated = RetrofitInstance.drawingApi.unlikeDrawing(drawingId, currentUser.id!!)
                            drawings = drawings.map { if (it.id == drawingId) updated else it }
                            selectedDrawing = updated
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to unlike drawing", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}


// DRAWING DIALOGUE SCREEN, FUNCTIONAL NOW

@Composable
fun DrawingDialog(
    drawing: Drawing,
    onDismiss: () -> Unit,
    onLike: (drawingId: Long) -> Unit,
    onUnlike: (drawingId: Long) -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Posted by ${drawing.user.username}") },
        modifier = Modifier.height(700.dp),

        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = drawing.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RectangleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Show the prompt
                Text(
                    text = "Prompt: ${drawing.prompt.text}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Show likes
                Text("Likes: ${drawing.likesCount}", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { onLike(drawing.id) }) { Text("Like") }
                    Button(onClick = { onUnlike(drawing.id) }) { Text("Unlike") }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )

}

@Preview
@Composable
fun leaderboardPreview(){
    val userViewModel = remember { UserViewModel() }
    leaderboard(navCon = rememberNavController(), userViewModel)
}

