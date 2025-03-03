package com.example.mealflow.random

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.mealflow.utils.Validator
import com.example.mealflow.viewModel.RegisterViewModel

//@Composable
//fun ShowPopup(text: String,showPopup:Boolean) {
//    var showPopup by remember { mutableStateOf(false) }
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        if (showPopup) {
//            Popup(
//                alignment = Alignment.Center,
//                onDismissRequest = { showPopup = false }
//            ) {
//                Box(
//                    modifier = Modifier
//                        .background(Color.White, shape = RoundedCornerShape(12.dp))
//                        .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
//                        Spacer(modifier = Modifier.height(12.dp))
//                        Button(onClick = { showPopup = false }) {
//                            Text("إغلاق")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//@Composable
//fun ShowPopup(text: String, showPopup: Boolean, onDismiss: () -> Unit) {
//    if (showPopup) {
//        Popup(
//            alignment = Alignment.Center,
//            onDismissRequest = onDismiss
//        ) {
//            Box(
//                modifier = Modifier
//                    .background(Color.White, shape = RoundedCornerShape(12.dp))
//                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
//                    .padding(16.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
//                    Spacer(modifier = Modifier.height(12.dp))
//                    Button(onClick = onDismiss) {
//                        Text("إغلاق")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true , showSystemUi = true)
//@Composable
//fun PreviewShowpopup()
//{
//    Column {
//        var showPopupError by remember { mutableStateOf(false) }
//        ShowPopup(text = "Skkkkkkkk",showPopup = false,    onDismiss = { showPopupError = false })
//    }
//}

//@Composable
//fun ShowErrorPopup(username:String,email:String,password:String)
//{
//    val errors:  MutableList<String> = mutableListOf()
//    val usernameError = Validator.validateUsername(username)
//    val emailError = Validator.validateEmail(email)
//    val passwordError = Validator.validatePassword(password)
//
//
//    if(usernameError != null)
//    {
//        errors.add(usernameError)
//    }
//    else if(emailError != null)
//    {
//        errors.add(emailError)
//    }
//    else if(passwordError != null)
//    {
//        errors.add(passwordError)
//    }
//    if (errors.isNotEmpty()) {
//        ShowPopup(text = errors.joinToString("\n"), showPopup = true)
//    }
//}
//@Composable
//fun ShowPopup(text: String, showPopup: Boolean, onDismiss: () -> Unit = {}) {
//    if (showPopup) {
//        AlertDialog(
//            onDismissRequest = onDismiss,
//            title = { Text(text = "Error") },
//            text = { Text(text = text) },
//            confirmButton = {
//                Button(onClick = onDismiss) {
//                    Text("OK")
//                }
//            }
//        )
//    }
//}

@Composable
fun ErrorPopupRegister(viewModel: RegisterViewModel) {
    val showErrorPopup by viewModel.showErrorPopup.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null) // تغيير إلى null

    if (showErrorPopup) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissErrorPopup() },
            title = { Text(text = "Error") },
            text = { Text(text = errorMessage ?: "Unknown error occurred") }, // حل المشكلة
            confirmButton = {
                Button(onClick = { viewModel.dismissErrorPopup() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun InputPopupDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    initialText: String = ""
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("خطأ في التسجيل") },
        text = { Text(text = initialText) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("حسناً")
            }
        }
    )
}
