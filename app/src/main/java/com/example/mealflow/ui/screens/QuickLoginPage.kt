package com.example.mealflow.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.R
import com.example.mealflow.database.token.TokenManager
import com.example.mealflow.network.quickLoginApi

@Composable
fun QuickLoginPage(
    context: Context,
    navController: NavController
) {
    val tokenManager = TokenManager(context)
    val refreshToken = tokenManager.getRefreshToken()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Button(onClick = {
//            navController.navigate("Login Page")
//        }) {
//            Text(text = "Nav")
//        }
        Text(
            text = "Save your login info?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "We'll save the login info for account to your device’s cloud backup, so you won’t need to enter it on this device or any device you restore.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp),
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(id = R.drawable.eye_view_icon),
            contentDescription = "Palestinian Flag",
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {
                quickLoginApi(refreshToken.toString(),context,navController)
                navController.navigate("Home Page")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(text = "Save", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
//                tokenManager.clearTokens()
                navController.navigate("Start Page")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(text = "Not now", color = Color.Black, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQuickScreen() {
    val context = LocalContext.current
    QuickLoginPage(context ,navController = rememberNavController())
}