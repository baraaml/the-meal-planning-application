package com.example.mealflow.ui.screens

import android.util.Log
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
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
import com.example.mealflow.buttons.BackButton
import com.example.mealflow.network.resetPasswordApi
import com.example.mealflow.utils.Validator
import com.example.mealflow.viewModel.ForgetPasswordViewModel

@Composable
fun ResetPasswordPage(navController: NavController, token: String?, viewModel: ForgetPasswordViewModel = viewModel()) {
    Log.d("ResetPasswordPage", "Received token: $token")

    // Store the token
    LaunchedEffect(token) {
        token?.let { viewModel.updateToken(it) }
    }

    val password by viewModel.password.observeAsState("")
    val passwordVisible by viewModel.passwordVisible.observeAsState(false)
    val repassword by viewModel.repassword.observeAsState("")
    val tokenValue by viewModel.token.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)
    // snackbarHostState and CoroutineScope
    val snackbarHostState = remember { SnackbarHostState() }

    val passwordError = Validator.validatePassword(password)
    var isFocusedPassword by remember { mutableStateOf(false) }

    // Determine if passwords match for validation
    val passwordsMatch = password == repassword && password.isNotEmpty()

    // Function to validate all inputs before API call
    val isFormValid = password.isNotEmpty() && passwordError == null && passwordsMatch

    Column {
        // ----------------------- Back Button ---------------------------
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp), horizontalArrangement = Arrangement.Start) {
            BackButton(onClick = {
                navController.popBackStack()
            })
        }

        Text(
            text = stringResource(id = R.string.ForgottenPassword),
            Modifier.padding(start = 20.dp, top = 80.dp, end = 20.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        //  ------------------ Password -----------------------
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
                focusedBorderColor = if (passwordError != null) Color.Red else Color.Blue,
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

        // Password Error Message
        if (isFocusedPassword && passwordError != null) {
            Text(
                text = passwordError,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 5.dp),
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        // Re-enter Password Field
        OutlinedTextField(
            value = repassword,
            onValueChange = { viewModel.updaterepassword(it) },
            label = { Text("Re-Enter your password") },
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp, end = 20.dp),
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )

        // Password match error
        if (repassword.isNotEmpty() && password != repassword) {
            Text(
                text = "Passwords do not match",
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 5.dp),
                color = Color.Red,
                fontSize = 12.sp
            )
        }

//        // API error message display
//        if (errorMessage?.isNotEmpty()) {
//            Text(
//                text = errorMessage,
//                modifier = Modifier
//                    .padding(start = 24.dp, end = 24.dp, top = 10.dp),
//                color = Color.Red,
//                fontSize = 14.sp
//            )
//        }

        // Submit Button with loading state
        Button(
            onClick = {
                if (isFormValid) {
                    // Use the stored token value
                    resetPasswordApi(tokenValue ?: "", password, navController, snackbarHostState)
                }
//                else {
//                    viewModel.setErrorMessage("Please fix the validation errors before submitting")
//                }
            },
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 100.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            enabled = isFormValid && !isLoading
        ) {
            if (isLoading) {
                // Show loading indicator
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(id = R.string.Submit),
                    color = Color.White
                )
            }
        }
    }
}// the password now is reset
// after this the user should be redirected to the home page

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ResetPasswordPagePreview()
{
    ResetPasswordPage(navController = rememberNavController(),"456456")
}