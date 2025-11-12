package com.example.drawingapp.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.UserViewModel
import com.example.drawingapp.network.RetrofitInstance
import kotlinx.coroutines.launch
import com.example.drawingapp.model.User

@Composable
fun OtherProfileScreen(navCon: NavController, userViewModel: UserViewModel, user: User) {

    val currentUser = userViewModel.currentUser
    var posts by remember { mutableStateOf<List<Drawing>>(emptyList()) }
    var selectedPost by remember { mutableStateOf<Drawing?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            name = user.username,
            modifier = Modifier
                .padding(10.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        ProfileSection(user)
        Spacer(modifier = Modifier.height(25.dp))
        FollowButton(
            user = user,
            onFollow = { user ->
                scope.launch {
                    try {
                        RetrofitInstance.userApi.followUser(currentUser!!.username, user)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to follow user", Toast.LENGTH_SHORT).show()
                    }
                }
           },
            onUnfollow = { user ->
                scope.launch {
                    try {
                        RetrofitInstance.userApi.unfollowUser(currentUser!!.username, user)

                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to unlike drawing", Toast.LENGTH_SHORT).show()
                }
            }
        })
        Spacer(modifier = Modifier.height(25.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { clip = false }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { clip = false }
            ) {
                items(posts.size) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        onClick = {
                            selectedPost = posts[index]
                        }
                    ) {
                        AsyncImage(
                            model = posts[index].imageUrl,
                            contentDescription = "Drawing Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
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
                                val updated = RetrofitInstance.drawingApi.likeDrawing(drawingId, user.id!!)
                                posts = posts.map { if (it.id == drawingId) updated else it }
                                selectedPost = updated
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to like drawing", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onUnlike = { drawingId ->
                        scope.launch {
                            try {
                                val updated = RetrofitInstance.drawingApi.unlikeDrawing(drawingId, user.id!!)
                                posts = posts.map { if (it.id == drawingId) updated else it }
                                selectedPost = updated
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to unlike drawing", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }

    }
}

@Composable
fun FollowButton(
    user: User,
    onFollow: (user: User) -> Unit,
    onUnfollow: (user: User) -> Unit
) {
    Button(onClick = { onFollow(user) }) { Text(text = "Follow") }
    Button(onClick = { onUnfollow(user) }) { Text(text = "Unfollow") }
}

