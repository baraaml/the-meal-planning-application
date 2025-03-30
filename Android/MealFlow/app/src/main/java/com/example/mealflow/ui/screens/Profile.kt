//package com.example.mealflow.ui.screens
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.*
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Tab
//import androidx.compose.material3.TabRow
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.layout.ContentScale
//import coil.compose.rememberAsyncImagePainter
//import com.example.mealflow.R
//
//@Composable
//fun ProfileScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(Color.Black, Color.DarkGray)
//                )
//            )
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    )  {
//        Image(
//            painter = rememberAsyncImagePainter(
//                model = "https://photobasement.com/wp-content/uploads/2017/04/this-is-a-photo.jpg",
//                error = painterResource(R.drawable.placeholder), // صورة افتراضية عند الخطأ
//                placeholder = painterResource(R.drawable.loading_placeholder) // صورة أثناء التحميل
//            ),
//            contentDescription = "صورة مجتمع ",
////            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .size(100.dp)
//                .clip(CircleShape)
//                .border(2.dp, Color.White, CircleShape)
//        )
////        AsyncImage(
////            model = "https://photobasement.com/wp-content/uploads/2017/04/this-is-a-photo.jpg",
////            contentDescription = "Profile Picture",
////            modifier = Modifier
////                .size(100.dp)
////                .clip(CircleShape)
////        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text("Berivan Kul", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
//        Text("bitesofberi", color = Color.Gray, fontSize = 16.sp)
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text("0 Following", color = Color.White, fontSize = 14.sp)
//            Spacer(modifier = Modifier.width(16.dp))
//            Text("23 Followers", color = Color.White, fontSize = 14.sp)
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Button(
//            onClick = {},
//            colors = ButtonDefaults.buttonColors( Color(0xFFFFA500)),
//            shape = RoundedCornerShape(8.dp)
//        ) {
//            Text("Follow", color = Color.Black)
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text("Simple recipes that taste delicious. 😋", color = Color.White, fontSize = 14.sp)
//        Spacer(modifier = Modifier.height(4.dp))
//        Text("www.bitesofberi.com", color = Color(0xFF1E90FF), fontSize = 14.sp)
//
//        Spacer(modifier = Modifier.height(16.dp))
//        val selectTab =
//        TabRow(selectedTabIndex = 0) {
//            Tab(selected = true, onClick = {}) {
//                Text("Activity", color = Color(0xFFFFA500), fontSize = 16.sp, fontWeight = FontWeight.Bold)
//            }
//            Tab(selected = false, onClick = {}) {
//                Text("Created", color = Color.White, fontSize = 16.sp)
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewProfileScreen() {
//    ProfileScreen()
//}
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mealflow.R
import com.example.mealflow.network.logoutApi
import com.example.mealflow.ui.components.CommunityList
import com.example.mealflow.ui.screens.PostCommunity
import com.example.mealflow.ui.screens.PostUser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_revert),
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Profile", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Black),
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_more),
                            contentDescription = "More",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Settings") }, onClick = { expanded = false })
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                expanded = false
                                logoutApi(
                                    context,
                                    navController,
                                    snackbarHostState
                                )
                            })
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black, Color.DarkGray)
                    )
                )
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = "https://photobasement.com/wp-content/uploads/2017/04/this-is-a-photo.jpg",
                        error = painterResource(R.drawable.placeholder), // صورة افتراضية عند الخطأ
                        placeholder = painterResource(R.drawable.loading_placeholder) // صورة أثناء التحميل
                    ),
                    contentDescription = "صورة مجتمع ",
//            contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
//                AsyncImage(
//                    model = "https://www.bitesofberi.com/wp-content/uploads/2021/01/Beri-Profile.jpg",
//                    contentDescription = "Profile Picture",
//                    modifier = Modifier
//                        .size(120.dp)
//                        .clip(CircleShape)
//                        .border(3.dp, Color.White, CircleShape)
//                )
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Berivan Kul", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("bitesofberi", color = Color.Gray, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("0 Following", color = Color.White, fontSize = 16.sp)
                        Text("23 Followers", color = Color.White, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(Color(0xFFFFA500)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Follow", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Simple recipes that taste delicious. 😋", color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("www.bitesofberi.com", color = Color(0xFF1E90FF), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                    Text("Activity", color = if (selectedTabIndex == 0) Color(0xFFFFA500) else Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                }
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                    Text("Created", color = if (selectedTabIndex == 1) Color(0xFFFFA500) else Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                }
                Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
                    Text("Menu", color = if (selectedTabIndex == 2) Color(0xFFFFA500) else Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> ActivityTabContent()
                1 -> CreatedTabContent()
                2 -> MenuTabContent()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfilePage(navController = rememberNavController())
}
@Composable
fun ActivityTabContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Activity Content Here", color = Color.White, fontSize = 16.sp)
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
        Text("Created Content Here", color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun MenuTabContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Menu Content Here", color = Color.White, fontSize = 16.sp)
    }
}
