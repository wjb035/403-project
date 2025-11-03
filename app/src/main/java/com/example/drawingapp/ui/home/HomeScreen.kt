package com.example.drawingapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drawingapp.R
import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navCon: NavController) {
    WhiteboardSimTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo",
                                modifier = Modifier.height(80.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f)
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.height(110.dp),
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { navCon.navigate("profileScreen") }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Profile",
                                modifier = Modifier.size(60.dp)
                            )
                            Text("Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        // Leaderboard Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { navCon.navigate("whiteboard") }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.leaderboard),
                                contentDescription = "Leaderboard",
                                modifier = Modifier.size(60.dp)
                            )
                            Text("Whiteboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        // Search Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { /* No action for now */ }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = "Search",
                                modifier = Modifier.size(60.dp)
                            )
                            Text("Search", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.white_layer),
                    contentDescription = "background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                // Middle of the screen is empty for future follower posts
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(navCon = rememberNavController())
}
