package com.example.mealflow.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.R
import com.example.mealflow.random.ErrorPopupRegister
import com.example.mealflow.random.InputPopupDialog
import com.example.mealflow.random.OrDivider
import com.example.mealflow.utils.Validator
import com.example.mealflow.viewModel.RegisterViewModel
import registerUser


// ----------------------- Register Page ---------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavController,viewModel: RegisterViewModel)
{
    // ----------------------- Variables ---------------------------
    val username by viewModel.username.observeAsState("")
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val repassword by viewModel.repassword.observeAsState("")
    val passwordVisible by viewModel.passwordVisible.observeAsState(false)
    val context = LocalContext.current
    var showPopupError by remember { mutableStateOf(false) }
    val usernameError = Validator.validateUsername(username)
    val emailError = Validator.validateEmail(email)
    val passwordError = Validator.validatePassword(password)
    var isFocusedUsername by remember { mutableStateOf(false) }
    var isFocusedEmail by remember { mutableStateOf(false) }
    var isFocusedPassword by remember { mutableStateOf(false) }
    var showErrorPopup by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp), horizontalArrangement = Arrangement.End) {
            Text(text = "Skip",
                Modifier
                    .clickable { }
                    .padding(20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
        // ----------------------- Join Text -----------------------------
        Text(
            text = stringResource(id = R.string.join),
            Modifier.padding(start = 20.dp, top = 40.dp ,end = 20.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        // ----------------------- InputFields ---------------------------
        // ---------------------------------------------------------------
        // Input field ------------------ UserName -----------------------
        OutlinedTextField(
            value = username,
            onValueChange = { viewModel.updateUsername(it) },
            label = { Text("Enter username") },
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (usernameError != null) Color.Red else Color.Blue,  // لون الحدود عند التركيز
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                .onFocusChanged { isFocusedUsername = it.isFocused }
        )
        // Text ------------------ Username Error -----------------------
        if (isFocusedUsername && usernameError != null)
        {
            Text(
                text = usernameError,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 5.dp),
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        // Input field ------------------ Email -----------------------
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Add an Email") },
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (emailError != null) Color.Red else Color.Blue,  // لون الحدود عند التركيز
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                .onFocusChanged { isFocusedEmail = it.isFocused }
        )
        // Text ------------------ Email Error -----------------------
        if (isFocusedEmail && emailError != null)
        {
            Text(
                text = emailError,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 5.dp),
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        // Input field ------------------ Password -----------------------
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Enter your password") },
            trailingIcon = {
                Icon(
                    painter = painterResource(
                        id = if (passwordVisible) {
                            R.drawable.eye_view_icon
                        } else {
                            R.drawable.eye_closed_icon
                        }
                    ),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { viewModel.togglePasswordVisibility() }
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (passwordError != null) Color.Red else Color.Blue,  // لون الحدود عند التركيز
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                .onFocusChanged { isFocusedPassword = it.isFocused },
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )
        // Text ------------------ Password Error -----------------------
        if (isFocusedPassword && passwordError != null)
        {
            Text(
                text = passwordError,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 5.dp),
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        // Text ------------------ Username Rules -----------------------
//        Text(
//            text = stringResource(id = R.string.UsernameRules),
//            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 5.dp),
//            fontSize = 12.sp
//        )
        // Text ------------------ Re_password -----------------------
//        Text(
//            text = stringResource(id = R.string.PasswordRules),
//            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 5.dp),
//            fontSize = 12.sp
//            )
        // Input field ------------------ Re_password -----------------------
//        OutlinedTextField(
//            value = repassword,
//            onValueChange = { viewModel.updateRepassword(it) },
//            label = { Text("Re-Enter your password") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 20.dp, top = 20.dp, end = 20.dp),
//            singleLine = true,
//            textStyle = TextStyle(color = Color.Black),
//            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
//        )
        //-----------------------------------------------------------------------------------------------------
        //-----------------------------------------------------------------------------------------------------
        // Button ------------------ Sign Up -----------------------

        Button(onClick = {
            if (viewModel.validateInputs(username, email, password)) {
                registerUser(context, username, email, password, navController,onError = { message ->
                    errorMessage = message
                    showErrorPopup = true
                })
            }
        } ,
            Modifier
                .padding(20.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Sign up",
                color = Color.White
            )
        }
        //----------------------- Button to go to the login page -----------------------
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "You already have an account? ")
            Text(text = "Login",
                Modifier
                    .clickable(onClick = {navController.navigate("Login Page")}
                    ),
                color = Color.Blue
            )
        }
        // ----------------------- Line with text in the middle ---------------------------
        OrDivider("OR")
        // ----------------------- Button to sign in with google ---------------------------
        Button(
            onClick = { /*TODO*/ } ,
            Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)

        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.google_icon_icons_com_62736
                ),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 10.dp)
            )
            Text(
                text = "Sign up using Google",
                color = Color.White
            )
        }
        ErrorPopupRegister(viewModel = viewModel)
        if (showErrorPopup) {
            InputPopupDialog(
                onDismiss = { showErrorPopup = false },
                onConfirm = { showErrorPopup = false },
                initialText = errorMessage
            )
        }

    }
}
// ----------------------- Function to preview RegisterPage ---------------------------
//---------------------------------------------------------------------------------
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewRegisterPage()
{
    val registerViewModel: RegisterViewModel = viewModel()
    RegisterPage(navController = rememberNavController(),registerViewModel)
}
//---------------------------------------------------------------------------------
//---------------------------------------------------------------------------------

//@Composable
//fun PreviewRegisterPage1()
//{
//    val usernameError = Validator.validateUsername("user_123")
//    val emailError = Validator.validateEmail("test@example.com")
//    val passwordError = Validator.validatePassword("Test@1234")
//    val otpError = Validator.validateOtp("123456")
//
//    if (usernameError != null) println(usernameError)
//    if (emailError != null) println(emailError)
//    if (passwordError != null) println(passwordError)
//    if (otpError != null) println(otpError)
//}
