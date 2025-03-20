package com.example.mealflow.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.mealflow.random.OrDivider
import com.example.mealflow.ui.navigation.Screen
import com.example.mealflow.utils.Validator
import com.example.mealflow.viewModel.LoginViewModel

// ----------------------- Login Page ---------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavController, viewModel: LoginViewModel = viewModel()) {
    // ----------------------- Variables ---------------------------
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val passwordVisible by viewModel.passwordVisible.observeAsState(false)
    val emailError = Validator.validateEmail(email)
    val passwordError = Validator.validatePassword(password)
    var isFocusedEmail by remember { mutableStateOf(false) }
    var isFocusedPassword by remember { mutableStateOf(false) }

    // Observe navigation to home screen
    val navigateToHome by viewModel.navigateToHome.observeAsState(false)

    // Navigate to home screen when navigateToHome is true
    if (navigateToHome) {
        LaunchedEffect(key1 = true) {
            navController.navigate("Home Page") {  // Changed from "Home Screen" to "Home Page" to match your existing code
                // Optional: pop up to the start destination to clear the back stack
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when navigating back
                launchSingleTop = true
                // Restore state when navigating back
                restoreState = true
            }
            viewModel.onHomeNavigationComplete()
        }
    }

    Column {
        // ----------------------- Skip Button ---------------------------
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp), horizontalArrangement = Arrangement.End) {
            Text(text = stringResource(id = R.string.Skip),
                Modifier
                    .clickable {
                        navController.navigate("Home Page") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    .padding(20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
        // ----------------------- Join Text -----------------------------
        Text(
            text = stringResource(id = R.string.Welcome_back),
            Modifier.padding(start = 20.dp, top = 40.dp ,end = 20.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        // ----------------------- InputFields ---------------------------
        // ---------------------------------------------------------------
        // Input field ------------------ Email -----------------------
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text(stringResource(id = R.string.EnterEmail)) },
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (emailError != null) Color.Red else Color.Blue,  // Color of border when focused
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
            label = { Text(stringResource(id = R.string.EnterPassword)) },
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (passwordError != null) Color.Red else Color.Blue,  // Color of border when focused
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
        // ----------------------- Forget Password Button ---------------------------
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp), horizontalArrangement = Arrangement.End) {
            Text(text = stringResource(id = R.string.ForgotPassword),
                Modifier
                    .clickable { navController.navigate("Forget Password Page") }
                    .padding(end = 25.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        }
        //-----------------------------------------------------------------------------------------------------
        //-----------------------------------------------------------------------------------------------------
        // Button ------------------ Log in -------------------------------------------------------------------
        Button(onClick = {
            viewModel.loginButton(email, password, navController)
        },
            Modifier
                .padding(20.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = stringResource(id = R.string.Login),
                color = Color.White
            )
        }
        // ----------------------- Button to go to the Register page -----------------------
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.DoNot_Account))
            Text(text = stringResource(id = R.string.Register),
                Modifier
                    .clickable(onClick = {navController.navigate("Register Page")}
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
            Icon(painter = painterResource(
                id = R.drawable.google_icon_icons_com_62736),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 10.dp))
            Text(
                text = "Sign up using Google",
                color = Color.White
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewLoginPage()
{
    LoginPage(navController = rememberNavController())
}
