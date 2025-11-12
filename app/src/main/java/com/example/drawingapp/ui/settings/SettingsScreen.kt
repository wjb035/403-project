package com.example.drawingapp.ui.settings

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drawingapp.R
import kotlin.system.exitProcess
import com.example.drawingapp.model.UserViewModel
import coil.request.ImageRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import coil.compose.AsyncImage
import com.example.drawingapp.network.RetrofitInstance
import com.example.drawingapp.network.UserApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun SettingsScreen(navCon: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val user by remember { mutableStateOf(userViewModel.currentUser) }
    var username by remember { mutableStateOf(user?.username ?: "") }
    var bio by remember { mutableStateOf(user?.bio ?: "") }
    var profilePictureUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var isEdit by rememberSaveable { mutableStateOf(false) }

    // PROFILE PICTURE AREA
    @Composable
    fun ProfilePictureSection (
        userViewModel: UserViewModel,
        userApi: UserApi
    ) {
        // File picker for the pfp
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                if (user?.id != null) {
                    uploadProfilePicture(
                        uri = uri,
                        userId = user!!.id!!,
                        context = context,
                        userApi = userApi,
                        userViewModel = userViewModel
                    )
                }
            }
        }

        // Observe ViewModel for live updates
        LaunchedEffect(userViewModel.currentUser?.profilePicture) {
            profilePictureUrl = userViewModel.currentUser?.profilePicture ?: ""
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // Display profile picture if available
            if (!profilePictureUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profilePictureUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFF6200EE), CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image!", color = Color.White)
                }
            }
        }

        // Button for changing picture
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Change Profile Picture")
        }
    }

    // MAIN SETTINGS COLUMN
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState()),
    ){
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(25.dp))
        ProfilePictureSection(
            userViewModel = userViewModel,
            userApi = RetrofitInstance.userApi
        )

        Spacer(modifier = Modifier.height(25.dp))
        Button(
            //modifier = Modifier.fillMaxWidth(),
            onClick = { isEdit = !isEdit }
        ) {
            Text(text = "Edit Profile")
        }
        if (isEdit) { EditBar() }

        Spacer(modifier = Modifier.height(30.dp))

        Text("Notification Settings")
        Spacer(Modifier.height(20.dp))
        NotificationSwitch("Friend's Posts")
        NotificationSwitch("New Followers")
        NotificationSwitch("Liked Posts")

        Spacer(modifier = Modifier.height(25.dp))

        NotificationButton()

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFF2B58)),
            onClick = {
                if (userViewModel.currentUser?.id != null) {
                    val userId = userViewModel.currentUser!!.id!!
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            RetrofitInstance.userSettingsApi.deleteUser(userId)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT)
                                    .show()
                                // Clear ViewModel
                                userViewModel.clearUser()
                                // Navigate to login or exit
                                navCon.navigate("login") {
                                    popUpTo(0) { inclusive = true } // clear back stack
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Delete failed: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        ) {
            Text(text = "DELETE ACCOUNT",
                modifier = Modifier,
                fontWeight = FontWeight.Bold,
                color = Color.Black)
        }
    }
}

@Composable
fun EditBar() {
    Column {
        OutlinedTextField(
            state = rememberTextFieldState(),
            label = { Text("Edit Name") },
            placeholder = { Text("Your name here...") }
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            state = rememberTextFieldState(),
            label = { Text("Edit Bio") },
            placeholder = { Text("Your bio here...") }
        )
    }
}

// Profile picture set
private fun uploadProfilePicture(uri: Uri, userId: Long, context: Context, userApi: UserApi, userViewModel: UserViewModel){
    val file = File(context.cacheDir, "temp_profile_pic")
    context.contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output -> input.copyTo(output) }
    }

    val part = MultipartBody.Part.createFormData(
        "file",
        file.name,
        file.asRequestBody("image/*".toMediaTypeOrNull())
    )
    val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

    // Upload with retrofit
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val updatedUser = userApi.uploadProfilePicture(part, userIdBody)
            withContext(Dispatchers.Main) {
                // Update viewmodel
                userViewModel.setUser(updatedUser)
                Toast.makeText(context, "Updated profile picture", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun NotificationSwitch(name: String) {
    var isChecked by rememberSaveable { mutableStateOf(false) }
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(name)
        Spacer(Modifier.width(40.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = !isChecked }
        )
    }
}
@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun NotificationButton() {
    val context = LocalContext.current

    Button(onClick = {
        showNotification(context)
    }) {
        Text("Show Notification")
    }
}
@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
private fun showNotification(context: Context) {
    val channelId = "channel_id"
    val channelName = "QuickDraw Channel"

    // Create channel for Android 8.0+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for demo notifications"
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Build notification
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("QuickDraw Notification")
        .setContentText("You have a new follower!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    // Show notification
    with(NotificationManagerCompat.from(context)) {
        notify(1001, builder.build())
    }
}