package com.example.whiteboardsim.ui

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.drawingapp.R
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.collections.map
import kotlin.collections.plus

@Composable
fun leaderboard(navCon: NavController) {
    Box(modifier=Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxWidth()
            .offset(x = 60.dp, y = 100.dp)){
            Row(
                Modifier.fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text="LEADERBOARD",
                    modifier=Modifier,
                    fontSize = 35.sp)

            }
        }
    }
    val list = listOf(
        "A", "B", "C", "D"
    ) + ((0..100).map { it.toString() })
    LazyColumn(modifier = Modifier
        .offset(0.dp, 150.dp)
    ) {
        items(items = list, itemContent = { item ->
            when (item) {
                /*"A" -> {
                    Text(text = item, style = TextStyle(fontSize = 80.sp))
                }
                "B" -> {
                    Button(onClick = {}) {
                        Text(text = item, style = TextStyle(fontSize = 80.sp))
                    }
                }
                "C" -> {
                    //Do Nothing
                }
                "D" -> {
                    Text(text = item)
                }*/
                else -> {
                    IconButton(onClick = {},
                        modifier = Modifier
                            .width(400.dp).height(300.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.image),
                            contentDescription = "Image",
                            modifier = Modifier.fillMaxSize()
                        )

                    }}
            }
        })
    }
}


