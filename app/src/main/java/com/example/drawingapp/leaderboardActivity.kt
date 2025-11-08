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

@Composable
fun leaderboard(navCon: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.white_layer),
            contentDescription = "background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        var showDialog by remember { mutableStateOf(false) }
        var postID by remember { mutableStateOf("NULL") }

        if (showDialog) {
            AlertDialog(onDismiss = { showDialog = false }, postID)
        }

        Column(modifier = Modifier.fillMaxSize()
           ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 50.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navCon.navigate("home") }) {
                    Image(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "Back to Home",
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = "Gallery",
                    modifier = Modifier,                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.size(48.dp)) // Balances the back button
            }

            val list = listOf(
                "A", "B", "C", "D"
            ) + ((0..100).map { it.toString() })
            Column(modifier = Modifier.fillMaxSize()
                .offset(0.dp, 50.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
                        items(items = list, itemContent = { item ->
                            IconButton(
                                onClick = {
                                    showDialog = true
                                    postID = item
                                },
                                modifier = Modifier
                                    .width(400.dp)
                                    .height(300.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.image),
                                    contentDescription = "Image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        })
                    }
                }
                }
        }
    }
}


//Primarily boilerplate code for displaying the necessary items- the image is currently kept as an IconButton and the like button
// has not been linked to any actual database functionality yet.
@Composable
fun AlertDialog(onDismiss: ()-> Unit, postID: String){
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        modifier = Modifier.height(700.dp),

        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                //Text(text = "Post Details")

            }
        },

        text = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(text="POST ID: " + postID, fontSize = 25.sp, color = MaterialTheme.colorScheme.onSurface)

                IconButton(onClick = {},
                    modifier = Modifier
                        .width(400.dp).height(500.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image),
                        contentDescription = "Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                    Button(
                    onClick = {},
                    modifier = Modifier.width(180.dp)
                ){
                    Text(text="Like Post?")
                }
            }
        }

    )

}

@Preview
@Composable
fun leaderboardPreview(){
    leaderboard(navCon = rememberNavController())
}

