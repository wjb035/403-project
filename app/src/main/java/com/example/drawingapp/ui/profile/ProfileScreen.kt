package com.example.drawingapp.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drawingapp.R
import com.example.drawingapp.ui.whiteboardtheme.WhiteboardSimTheme
import com.example.drawingapp.ui.whiteboardtheme.md_theme_dark_onPrimary

@Composable
fun ProfileScreen(navCon: NavController) {
    WhiteboardSimTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.white_layer),
                contentDescription = "background",
                contentScale = ContentScale.FillBounds
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                name = "profile_screen",
            )
            ProfileSection()
            PostSection(
                posts = listOf(
                    painterResource(id = R.drawable.example_drawing_1),
                    painterResource(id = R.drawable.example_drawing_2),
                    painterResource(id = R.drawable.example_drawing_3),
                    painterResource(id = R.drawable.example_drawing_4),
                    painterResource(id = R.drawable.example_drawing_5),
                    painterResource(id = R.drawable.example_drawing_6),
                ),
                modifier = Modifier.fillMaxWidth()
            )

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
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.back_arrow),
            contentDescription = "Back",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = name,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ProfileSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 15.dp)
        ) {
            RoundImage(
                image = painterResource(id = R.drawable.generic_pfp),
                modifier = Modifier
                    .size(100.dp)
                    .weight(3f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatSection(modifier = Modifier.weight(7f))
        }
        ProfileDescription(
            description = "This is an example description for viewing purposes"
        )
        ButtonSection(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp))
    }
}

@Composable
fun RoundImage(
    image: Painter,
    modifier : Modifier = Modifier
) {
    Image(
        painter = image,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape)
    )
}

@Composable
fun StatSection(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {
        ProfileStat(numberText = "6", text = "Posts")
        ProfileStat(numberText = "100K", text = "Followers")
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
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
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
            lineHeight = lineHeight,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun ButtonSection(
    modifier: Modifier = Modifier
) {
    val minWidth = 100.dp
    val height = 40.dp
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        ActionButton(
            text = "Following",
            modifier = Modifier
                .defaultMinSize(minWidth = minWidth)
                .height(height)
                .clip(RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(
                width = 5.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
    ) {
        if(text != null) {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        if(icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PostSection(
    posts: List<Painter>,
    modifier: Modifier = Modifier
) {
    var selectedPost by remember { mutableStateOf<Painter?>(null) }

    // The key fix is this BoxWithConstraints + clip = false
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(posts.size) { index ->
            Image(
                painter = posts[index],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .border(1.dp, MaterialTheme.colorScheme.surface)
                    .clickable { selectedPost = posts[index] }
            )
        }
    }

    // Overlay viewer — it will now draw freely
    if (selectedPost != null) {
        PhotoClick(
            post = selectedPost!!,
            onDismiss = { }
        )
    }
}


@Composable
fun PhotoClick(
    post: Painter,
    onDismiss: () -> Unit
) {
    var isToggled by rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
        )

        Image(
            painter = post,
            contentDescription = "Selected image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        IconButton(
            onClick = { isToggled = !isToggled },
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .offset(y = 200.dp)
        ) {
            Image(
                painter = if (isToggled) painterResource(R.drawable.filled_heart)
                else painterResource(R.drawable.empty_heart),
                contentDescription = "Heart",
                modifier = Modifier
                    .size(25.dp)
            )
        }

    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navCon = NavController(LocalContext.current))
}
