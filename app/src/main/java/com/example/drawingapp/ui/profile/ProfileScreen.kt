package com.example.drawingapp.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.UserViewModel
import com.example.drawingapp.network.RetrofitInstance
import kotlinx.coroutines.launch
import com.example.drawingapp.model.User

@Composable
fun ProfileScreen(navCon: NavController, userViewModel: UserViewModel) {

    val currentUser = userViewModel.currentUser
    var posts by remember { mutableStateOf<List<Drawing>>(emptyList()) }
    var selectedPost by remember { mutableStateOf<Drawing?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }


    // FETCH DRAWIGNS!
    LaunchedEffect(currentUser?.id) {
        isLoading = true
        currentUser?.id?.let { userId ->
            scope.launch {
                try {
                    posts = RetrofitInstance.drawingApi.getUserDrawings(userId)
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to load drawings", Toast.LENGTH_SHORT).show()
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            name = userViewModel.getUser()?.username ?: "Unable to get name",
            modifier = Modifier
                .padding(10.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        ProfileSection(currentUser)
        Spacer(modifier = Modifier.height(25.dp))


        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { clip = false }
        ) {
            // Loading indicator
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { clip = false }
                ) {
                    items(posts.size) { index ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            onClick = {
                                selectedPost = posts[index]
                            }
                        ) {
                            Box{
                                AsyncImage(
                                    model = posts[index].imageUrl,
                                    contentDescription = "Drawing Image",
                                    modifier = Modifier.fillMaxWidth().height(250.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(Color.Black.copy(alpha = 0.5f), shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${posts[index].likesCount} ❤️",
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (selectedPost != null) {
                    PhotoClick(
                        post = selectedPost!!,
                        onDismiss = { selectedPost = null },
                        onLike = { drawingId ->
                            scope.launch {
                                try {
                                    val updated = RetrofitInstance.drawingApi.likeDrawing(drawingId, currentUser!!.id!!)
                                    posts = posts.map { if (it.id == drawingId) updated else it }
                                    selectedPost = updated
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Already liked drawing", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onUnlike = { drawingId ->
                            scope.launch {
                                try {
                                    val updated = RetrofitInstance.drawingApi.unlikeDrawing(drawingId, currentUser!!.id!!)
                                    posts = posts.map { if (it.id == drawingId) updated else it }
                                    selectedPost = updated
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Can't unlike drawing", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }

    }
}

@Composable
fun TopBar(
    name: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
    ) {

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = name,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.weight(1f))

    }
}

@Composable
fun ProfileSection(
    user: User?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            RoundImage(
                imageUrl = user!!.profilePicture,
                modifier = Modifier
                    .size(100.dp)
                    .weight(3f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatSection(user, modifier = Modifier.weight(7f))
        }
        ProfileDescription(
            description = user!!.bio
        )
    }
}

@Composable
fun RoundImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(3.dp, Color(0xFF6200EE), CircleShape)
            .padding(3.dp)
            .clip(CircleShape)
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // fallback gray circle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("No Image", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StatSection(
    user: User?,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {
        ProfileStat(numberText = user!!.followers.size.toString(), text = "Followers")
    }
}

@Composable
fun ProfileStat(
    numberText: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = numberText,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text)
    }
}

@Composable
fun ProfileDescription(
    description: String,
) {
    val letterSpacing = 0.5.sp
    val lineHeight = 20.sp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        Text(
            text = description,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
    }
}

@Composable
fun PhotoClick(
    post: Drawing,
    onDismiss: () -> Unit,
    onLike: (drawingId: Long) -> Unit,
    onUnlike: (drawingId: Long) -> Unit
) {
    var isToggled by rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss)
        )

        AsyncImage(
            model = post.imageUrl,
            contentDescription = "Selected Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.Center)
                .clip(RectangleShape),
            contentScale = ContentScale.Fit
        )

        // Like count inside a background box
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .background(Color.Black.copy(alpha = 0.75f), shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "${post.likesCount} ❤️",
                color = Color.White,
                fontSize = 24.sp
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Button(onClick = { onLike(post.id) }) { Text("❤️") }
            Button(onClick = { onUnlike(post.id) }) { Text("\uD83D\uDDA4") }
        }

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("Back")
        }

    }

}

