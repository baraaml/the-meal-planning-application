package com.example.mealflow.ui.screens

import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NotListedLocation
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.WrongLocation
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mealflow.R
import com.example.mealflow.network.logoutApi


@Composable
fun ProfilePage(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(Color.DarkGray, Color.White)
//                )
//            ),
                ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBarProfile(
            context,
            navController,
            snackbarHostState
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://photobasement.com/wp-content/uploads/2017/04/this-is-a-photo.jpg",
                    error = painterResource(R.drawable.placeholder), // Default image on error
                    placeholder = painterResource(R.drawable.loading_placeholder) // Image while loading
                ),
                contentDescription = "Community image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
            Column(horizontalAlignment = Alignment.Start) {
                Text("Abdelrahman", color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.sfpro)))
//                Text("123456789", color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("0 Following", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                    Text("23 Followers", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Like",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Location", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(Color(0xFFFFA500)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Follow", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Simple recipes that taste delicious. 😋", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
//            Text("www.bitesofberi.com", color = Color(0xFF1E90FF), fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()) {
            Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                Text("Activity", color = if (selectedTabIndex == 0) Color(0xFFFFA500) else Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
            }
            Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                Text("Recipes", color = if (selectedTabIndex == 1) Color(0xFFFFA500) else Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
            }
//            Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
//                Text("Menu", color = if (selectedTabIndex == 2) Color(0xFFFFA500) else Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
//            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (selectedTabIndex) {
            0 -> ActivityTabContent()
            1 -> CreatedTabContent()
            2 -> MenuTabContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfilePage(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarProfile(
    context: Context,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Handle back action */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Profile",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Box to anchor the dropdown menu
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .align(Alignment.TopEnd), // Align to the top-end (right) of the parent Box
                        offset = DpOffset(x = 0.dp, y = 0.dp) // You can adjust this if needed
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings", fontSize = 16.sp) },
                            onClick = { expanded = false },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        DropdownMenuItem(
                            text = { Text("Logout", fontSize = 16.sp, color = Color.Red) },
                            onClick = {
                                expanded = false
                                logoutApi(context, navController, snackbarHostState)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityTabContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(1) {
                PostCommunity()
                PostUser()
                PostUser()
                PostUser()
                PostUser()
                PostCommunity()
                PostCommunity()
                PostCommunity()
                PostCommunity()
                PostUser()
            }
        }
    }
}

@Composable
fun CreatedTabContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Created Content Here", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)

    }
}

@Composable
fun MenuTabContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Menu Content Here", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
    }
}
