package com.example.mealflow.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.example.mealflow.network.ResetPasswordApi
import com.example.mealflow.random.OrDivider
import com.example.mealflow.utils.Validator
import com.example.mealflow.viewModel.ForgetPasswordViewModel
import com.example.mealflow.viewModel.LoginViewModel


// ----------------------- Login Page ---------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordPage(navController: NavController, token: String?,viewModel: ForgetPasswordViewModel = viewModel())
{
    Log.d("ResetPasswordPage", "Received token: $token")
    // 🔥 تخزين `token` في الـ ViewModel عند تشغيل الصفحة
    LaunchedEffect(token) {
        token?.let { viewModel.updateToken(it) }
    }
    // ----------------------- Variables ---------------------------
    val password by viewModel.password.observeAsState("")
    val passwordVisible by viewModel.passwordVisible.observeAsState(false)
    val passwordError = Validator.validatePassword(password)
    var isMatchPassword by remember { mutableStateOf(false) }
    var isFocusedPassword by remember { mutableStateOf(false) }
    val repassword by viewModel.repassword.observeAsState("")
    val token by viewModel.token.observeAsState("")



    Box{
        Column {
            // ----------------------- Skip Button ---------------------------
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp), horizontalArrangement = Arrangement.Start) {
                BackButton(onClick = { /*TODO*/ })
            }
            // ----------------------- Join Text -----------------------------
            Text(
                text = stringResource(id = R.string.ForgottenPassword),
                Modifier.padding(start = 20.dp, top = 80.dp, end = 20.dp),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
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
            // Text ------------------ Re_password -----------------------
//            Text(
//                text = stringResource(id = R.string.PasswordRules),
//                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 5.dp),
//                fontSize = 12.sp
//            )
            //Input field ------------------ Re_password -----------------------
                    OutlinedTextField(
                        value = repassword,
                        onValueChange = { viewModel.updateRepassword(it) },
                        label = { Text("Re-Enter your password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                    )

            // Text ------------------ Password Error -----------------------
            if (password != repassword)
            {
                Text(
                    text = "Passwords do not match",
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 5.dp),
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
            //-----------------------------------------------------------------------------------------------------
            //-----------------------------------------------------------------------------------------------------
            // Button ------------------ Log in -------------------------------------------------------------------
            Button(
                onClick = {
                    //ResetPasswordApi(token,password,navController,viewModel)
                    ResetPasswordApi(token ?: "", password, navController, viewModel)
                },
                Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 100.dp)
                    .align(alignment = Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = stringResource(id = R.string.Submit),
                    color = Color.White
                )
            }
        }
    }
}

//------------------------------------------------------------------
//------------------------------------------------------------------
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ResetPasswordPagePreview()
{
    //ResetPasswordPage(navController = rememberNavController())
}
//---------------------------------------------------------------
//------------------------------------------------------------------