package com.example.drawingapp

import android.os.Bundle

import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme
import com.example.drawingapp.ui.whiteboard
import com.example.drawingapp.prompter.PromptScreen
import com.example.drawingapp.ui.home.HomeScreen
import com.example.drawingapp.ui.profile.ProfileScreen
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.activity.ComponentActivity
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drawingapp.loginscreen.LoginScreen
import com.example.drawingapp.loginscreen.RegisterScreen
import com.example.drawingapp.ui.search.SearchScreen
import com.example.drawingapp.ui.settings.SettingsScreen
import com.example.drawingapp.model.UserViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {

    @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhiteboardSimTheme {
                val navController = rememberNavController()
                val userViewModel: UserViewModel = viewModel()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route
                fun getIconForItem(item: String): Int {
                    return when (item) {
                        "Home" -> R.drawable.home // Replace with your actual drawable resource
                        "Profile" -> R.drawable.profile
                        "Gallery" -> R.drawable.gallery
                        "Search" -> R.drawable.search_icon
                        "Settings" -> R.drawable.setting_icon
                        else -> R.drawable.image
                    }
                }
                Scaffold(
                    bottomBar = {
                        if (currentRoute in listOf("home", "search", "gallery", "profile", "settings", "whiteboard", "prompt")){
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                val items = listOf("Home", "Search", "Gallery", "Profile", "Settings")
                                
                                items.forEach { route ->
                                    NavigationBarItem(
                                        
                                        icon = {Image(
                                            painter = painterResource(id = getIconForItem(route)),
                                            contentDescription = route,
                                            modifier = Modifier.size(24.dp)
                                            )

                                        },
                                        label = { Text(route,color = MaterialTheme.colorScheme.onPrimary) },
                                        selected = false,
                                        onClick = { navController.navigate(route.lowercase()) },
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
                        composable(route = "splash") {
                            SplashScreen(navController = navController)
                        }
                        composable(route = "home"){
                            HomeScreen(navCon=navController)
                        }
                        composable(route = "profile"){
                            ProfileScreen(navCon=navController)
                        }
                        composable(route="gallery"){
                            leaderboard(navCon=navController, userViewModel = userViewModel)
                        }

                        // not part of the nav bar
                        composable(route = "prompt"){
                            PromptScreen(navCon=navController)
                        }
                        composable(route = "whiteboard"){
                            whiteboard(navCon=navController)
                        }
                        composable(route = "login"){
                            LoginScreen(navCon =navController, userViewModel = userViewModel)
                        }
                        composable(route = "register"){
                            RegisterScreen(navCon=navController, userViewModel = userViewModel)
                        }
                        composable(route = "search") {
                            SearchScreen(navCon=navController)
                        }
                        composable(route = "settings") {
                            SettingsScreen(navCon=navController)
                        }

                    }
                }


            }
        }

    }

}