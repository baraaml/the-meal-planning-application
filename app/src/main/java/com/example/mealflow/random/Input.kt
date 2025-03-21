//package com.example.mealflow.random
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.Icon
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.mealflow.R
//
//@Composable
//fun registerInput():Triple<String, String, String>
//{
//    var username by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var repassword by remember { mutableStateOf("") }
//    var passwordVisible by remember { mutableStateOf(false) }
//    OutlinedTextField(
//        value = username,
//        onValueChange = { newText -> username = newText },
//        label = { Text("Enter username") },
//        singleLine = true,
//        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
//    )
//    // Input field
//    OutlinedTextField(
//        value = email,
//        onValueChange = { newText -> email = newText },
//        label = { Text("Add an Email") },
//        singleLine = true,
//        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
//    )
//    // حقل كلمة السر
//    OutlinedTextField(
//        value = password,
//        onValueChange = { newPassword -> password = newPassword },
//        label = { Text("Enter your password") },
//        trailingIcon = {
//            Icon(
//                painter = painterResource(
//                    id = if (passwordVisible) {
//                        R.drawable.eye_view_icon
//                    } else {
//                        R.drawable.eye_closed_icon
//                    }
//                ),
//                contentDescription = null,
//                tint = Color.Gray,
//                modifier = Modifier
//                    .size(28.dp)
//                    .clickable { passwordVisible = !passwordVisible }
//            )
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
//        singleLine = true,
//        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
//        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
//    )
//    // حقل كلمة السر
//    OutlinedTextField(
//        value = repassword,
//        onValueChange = { newPassword -> repassword = newPassword },
//        label = { Text("Re-Enter your password") },
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
//        singleLine = true,
//        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
//        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
//    )
//    return Triple(username,email,password)
//}
////----------------------------------------------------------
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewRegisterInput()
//{
//    Column {
//        registerInput()
//    }
//}