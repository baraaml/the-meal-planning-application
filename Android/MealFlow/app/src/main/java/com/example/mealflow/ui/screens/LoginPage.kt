package com.example.mealflow.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import com.example.mealflow.network.loginApi
import com.example.mealflow.random.OrDivider
import com.example.mealflow.utils.Validator
import com.example.mealflow.viewModel.LoginViewModel
import kotlinx.coroutines.launch

// ----------------------- Login Page ---------------------------
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

    // snackbarHostState and CoroutineScope
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Observe loading state for login
    val isLoading by viewModel.isLoading.observeAsState(false)

//    // Observe login message for errors
//    val loginMessage by viewModel.loginMessage.observeAsState("")

    // Observe navigation to home screen
    val navigateToHome by viewModel.navigateToHome.observeAsState(false)

    val context = LocalContext.current

    // Navigate to home screen when navigateToHome is true
    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            navController.navigate("Home Page") {
                // Pop up to the start destination to clear the back stack
                popUpTo("Start Page") {
                    inclusive = true
                }
                // Avoid multiple copies of the same destination
                launchSingleTop = true
            }
            viewModel.onHomeNavigationComplete()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.ime) // Adjusts the position based on the keyboard height.
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            // ----------------------- Skip Button ---------------------------
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(id = R.string.Skip),
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
                    fontFamily = FontFamily(Font(R.font.sfmed)),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            // ----------------------- Join Text -----------------------------
            Text(
                text = stringResource(id = R.string.Welcome_back),
                Modifier.padding(start = 20.dp, top = 40.dp, end = 20.dp),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.sf_pro_rounded_heavy))
            )
            // ----------------------- InputFields ---------------------------
            // Input field ------------------ Email -----------------------
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.updateEmail(it) },
                label = {
                    Text(
                        stringResource(id = R.string.EnterEmail),
                        fontFamily = FontFamily(Font(R.font.sflightit))
                    ) },
                singleLine = true,
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (emailError != null) Color.Red else Color.Blue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                    .onFocusChanged { isFocusedEmail = it.isFocused }
            )
            // Email Error
            if (isFocusedEmail && emailError != null) {
                Text(
                    text = emailError,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 5.dp),
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
            // Input field ------------------ Password -----------------------
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.updatePassword(it) },
                label = {
                    Text(
                        stringResource(id = R.string.EnterPassword),
                        fontFamily = FontFamily(Font(R.font.sflightit))
                    ) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible)
                            {R.drawable.eye_view_icon}
                            else
                            {R.drawable.eye_closed_icon}
                        ),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { viewModel.togglePasswordVisibility() }
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (passwordError != null) Color.Red else Color.Blue
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
            // Password Error
            if (isFocusedPassword && passwordError != null) {
                Text(
                    text = passwordError,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 5.dp),
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
            // ----------------------- Forget Password Button ---------------------------
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(id = R.string.ForgotPassword),
                    Modifier
                        .clickable { navController.navigate("Forget Password Page") }
                        .padding(end = 25.dp),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.sflight)),
                    color = MaterialTheme.colorScheme.error                )
            }
            // ----------------------- Log in Button ---------------------------
            Button(
                onClick = {
                    coroutineScope.launch {
                        loginApi(context,email, password, navController, snackbarHostState)
                    }
                },
                Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                enabled = !isLoading // Disable button during loading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.Login),
                        color = Color.White
                    )
                }
            }
            // ----------------------- Button to go to the Register page -----------------------
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    text = stringResource(id = R.string.DoNot_Account),
                    fontFamily = FontFamily(Font(R.font.sflightit)),
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = stringResource(id = R.string.Register),
                    Modifier.clickable { navController.navigate("Register Page") },
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = FontFamily(Font(R.font.sfmed))
                )
            }
            // ----------------------- Line with text in the middle ---------------------------
            OrDivider("OR")
            // ----------------------- Sign up using Google ---------------------------
            Button(
                onClick = { /*TODO*/ },
                Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google_icon_icons_com_62736),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp).padding(end = 10.dp)
                )
                Text(
                    text = "Sign up using Google",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.sfmed))
                )
            }
        }
    }
}

//------------------------------------------------------------------
//------------------------------------------------------------------
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewLoginPage()
{
    LoginPage(navController = rememberNavController())
}
//------------------------------------------------------------------
//------------------------------------------------------------------
