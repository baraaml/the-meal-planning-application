package com.example.mealflow.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.buttons.DynamicButton
import com.example.mealflow.R
import com.example.mealflow.random.BottomBar

//// ----------------------- Home Page ---------------------------
//@Composable
//fun HomePage(navController: NavController) {
//
//    // ----------------------- Image containing background for the main page ---------------------------
//    Image(
//        painter = painterResource(id = R.drawable.background_main_page),
//        contentDescription = null,
//        contentScale = ContentScale.Crop,
//        modifier = Modifier.fillMaxSize()
//    )
//    // ----------------------- Box contains a button to log in and register ---------------------------
//    Box(
//        modifier = Modifier
//    ) {
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp)
//            )
//            {
//
//            }
//            Image(
//                painter = painterResource(id = R.drawable.mealflow),
//                contentDescription = null,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(bottom = 32.dp),
//            verticalArrangement = Arrangement.Bottom,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // ----------------------- Button Log in ---------------------------
//            DynamicButton(
//                onClick = {
//                    navController.navigate("Log in")
//                },
//                textOnButton = "Log in",
//                buttonWidthDynamic = 232,
//                modifier = Modifier
//                    .padding(top = 35.dp)
//            )
//            // ----------------------- Button register ---------------------------
//            DynamicButton(
//                onClick = {
//                    navController.navigate("Register")
//                },
//                textOnButton = "Register",
//                buttonWidthDynamic = 232,
//                modifier = Modifier
//                    .padding(top = 50.dp)
//            )
//            Spacer(modifier = Modifier.padding(bottom = 70.dp))
//
//        }
//    }
//}

//@Composable
//fun HomePage(navController: NavController)
//{
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ){
//        Column(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            Text(text = "abdo")
//        }
//        Row(verticalAlignment = Alignment.Bottom) {
//            BottomBar()
//        }
//    }
//}
//
//
//// ----------------------- Function to preview HomePage ---------------------------
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewHomePage()
//{
//    HomePage(navController = rememberNavController())
//}

@Composable
fun HomePage(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp) // لتجنب تداخل المحتوى مع الـ BottomBar
        ) {
            Text(text = "abdo")
        }

        // الحل الصحيح: وضع BottomBar داخل Box مع align(Alignment.BottomCenter)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // تثبيت الـ BottomBar في الأسفل
        ) {
            BottomBar()
        }
    }
}

// ----------------------- Function to preview HomePage ---------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomePage() {
    HomePage(navController = rememberNavController())
}
