package com.example.mealflow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterInput1()
{
    var text by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    // Input field
    OutlinedTextField(
        value = text,
        onValueChange = { newText -> text = newText },
        label = { Text("Add an Email") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
    )

    // حقل كلمة السر
    OutlinedTextField(
        value = password,
        onValueChange = { newPassword -> password = newPassword },
        label = { Text("Enter your paswoed") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
        singleLine = true,
        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
    )
    // حقل كلمة السر
    OutlinedTextField(
        value = repassword,
        onValueChange = { newPassword -> repassword = newPassword },
        label = { Text("Re-Enter your paswoed") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        singleLine = true,
        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
    )
    Row {
        Checkbox(
            checked = passwordVisible,
            onCheckedChange = { passwordVisible = it },
            modifier = Modifier.padding(start = 5.dp)
        )
        //Text(text = "Show Password", modifier = Modifier.padding(start = 8.dp))
        Text(text = "show password", Modifier.padding(top = 15.dp))
    }
}


// حقل كلمة السر
//OutlinedTextField(
//value = password,
//onValueChange = { newPassword -> password = newPassword },
//label = { Text("Enter your paswoed") },
//modifier = Modifier
//.fillMaxWidth()
//.padding(start = 20.dp, top = 20.dp, end = 20.dp),
//singleLine = true,
//textStyle = TextStyle(color = Color.Black), // تحديد لون النص
//visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
//)
