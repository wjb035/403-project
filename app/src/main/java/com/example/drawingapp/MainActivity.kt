package com.example.drawingapp

import android.os.Bundle

import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme
import com.example.drawingapp.ui.whiteboard
import com.example.drawingapp.prompter.PromptScreen
import com.example.drawingapp.ui.gallery.GalleryScreen
import com.example.drawingapp.ui.home.HomeScreen
import com.example.drawingapp.ui.profile.ProfileScreen
import com.example.drawingapp.ui.profile.OldProfileScreen
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.activity.ComponentActivity
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import com.example.drawingapp.leaderboard
import com.example.drawingapp.loginscreen.LoginScreen
import com.example.drawingapp.loginscreen.RegisterScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhiteboardSimTheme {
                // Creates a navController for switching between views. You start
                // off in the home menu.

                val navController = rememberNavController()
                // lets you track which screren is currently visible
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route
                Scaffold(
                    bottomBar = {
                        if (currentRoute in listOf("home", "gallery", "profile", "whiteboard", "prompt")){
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                val items = listOf("Home", "Gallery", "Profile")
                                items.forEach { route ->
                                    NavigationBarItem(
                                        icon = {//put something here later
                                        },
                                        label = { Text(route,color = MaterialTheme.colorScheme.onPrimary) },
                                        selected = false,
                                        onClick = { navController.navigate(route) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                    )
                                }
                        }

                        }
                    }
                ) {innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ){
                        // All the different routes. Each one is given a reference
                        // to the nav controller so you can navigate between areas.
                        composable(route = "home"){
                            HomeScreen(navCon=navController)
                        }
                        composable(route = "profile"){
                            ProfileScreen(navCon=navController)
                        }
                        composable(route="gallery"){
                            leaderboard(navCon=navController)
                        }

                        // not part of the nav bar
                        composable(route = "prompt"){
                            PromptScreen(navCon=navController)
                        }
                        composable(route = "whiteboard"){
                            whiteboard(navCon=navController)
                        }
                        composable(route = "login"){
                            LoginScreen(navCon=navController)
                        }
                        composable(route = "register"){
                            RegisterScreen(navCon=navController)
                        }

                    }
                }


            }
        }

    }

}