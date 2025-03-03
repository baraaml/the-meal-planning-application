package com.example.mealflow.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.R
import com.example.mealflow.buttons.DynamicButton


// ----------------------- Register Page ---------------------------
@Composable
fun StartPage(navController: NavController)
{
    Box(modifier = Modifier.fillMaxSize()) {
        // ----------------------- Title Text -----------------------------
        Text(
            text = stringResource(id = R.string.Header),
            Modifier
                .padding(start = 20.dp, top = 100.dp, end = 20.dp)
                .align(Alignment.CenterStart),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Column(
            modifier = Modifier.align(alignment = Alignment.BottomCenter)
        ) {
            // Button ------------------ Get Started -----------------------
            Button(
                onClick = {navController.navigate("Register Page")} ,
                Modifier
                    .padding(20.dp)
                    .align(alignment = Alignment.CenterHorizontally)
                    .width(150.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = stringResource(id = R.string.Get_Started),
                    color = Color.White
                )
            }
            // Row ------------------ Login -----------------------
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(bottom = 100.dp)
            ) {
                // ----------------------- Text -----------------------------
                Text(text = stringResource(id = R.string.Already_member))
                // ----------------------- Login (Clickable Text) -----------------------------
                Text(text = "Login",
                    Modifier
                        .clickable(onClick = {navController.navigate("Login Page")}
                        ),
                    color = Color.Blue
                )
            }
        }
    }
}
// ----------------------- Function to preview StartPage ---------------------------
//------------------------------------------------------------------
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewStartPage()
{
    StartPage(navController = rememberNavController())
}
//---------------------------------------------------------------
//---------------------------------------------------------------