package com.example.drawingapp.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import com.example.drawingapp.R
import kotlin.system.exitProcess

@androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun SettingsScreen(navCon: NavController) {

    var isEdit by rememberSaveable { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
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

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { exitProcess(0) }
        ) {
            Text(text = "DELETE ACCOUNT")
        }
    }
}

@Composable
fun EditBar() {
    Column {
        OutlinedTextField(
            state = rememberTextFieldState(),
            label = { Text("Edit Name") },
            placeholder = { Text("edit name") }
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            state = rememberTextFieldState(),
            label = { Text("Edit Bio") },
            placeholder = { Text("edit bio") }
        )
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
@androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun NotificationButton() {
    val context = LocalContext.current

    Button(onClick = {
        showNotification(context)
    }) {
        Text("Show Notification")
    }
}
@androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
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
        .setContentText("You just got a new follower!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    // Show notification
    with(NotificationManagerCompat.from(context)) {
        notify(1001, builder.build())
    }
}