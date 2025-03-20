package com.example.mealflow.ui.screens

import android.util.Log
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import com.example.mealflow.ui.theme.Red
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
import com.example.mealflow.network.ForgetPasswordRequest
import com.example.mealflow.network.forgetPasswordApi
import com.example.mealflow.random.OrDivider
import com.example.mealflow.utils.Validator
import com.example.mealflow.viewModel.ForgetPasswordViewModel
import com.example.mealflow.viewModel.LoginViewModel


// ----------------------- Forget Password Page ---------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordPage(navController: NavController,viewModel: ForgetPasswordViewModel = viewModel())
{
    // ----------------------- Variables ---------------------------
    val email by viewModel.email.observeAsState("")
    val emailError = Validator.validateEmail(email)
    var isFocusedEmail by remember { mutableStateOf(false) }

        Column {
            // ----------------------- Back Button ---------------------------
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp), horizontalArrangement = Arrangement.Start) {
                BackButton(onClick = {
                    navController.navigate("Home Page")
                })
            }
            // ----------------------- Forgotten Password Text -----------------------------
            Text(
                text = stringResource(id = R.string.ForgottenPassword),
                Modifier.padding(10.dp),
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
                    focusedBorderColor = if (emailError != null) Color.Red else Color.Blue,  // لون الحدود عند التركيز
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
                    .onFocusChanged { isFocusedEmail = it.isFocused }
            )
            // Text ------------------ Email Error -----------------------
            if (isFocusedEmail && emailError != null) {
                Text(
                    text = emailError,
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 5.dp),
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
            //-----------------------------------------------------------------------------------------------------
            //-----------------------------------------------------------------------------------------------------
            // Button ------------------ Reset your Password -------------------------------------------------------------------
            Button(
                onClick = {
                    if(emailError == null)
                    {
                        forgetPasswordApi(email,navController,viewModel)
                    }
                },
                Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    .align(alignment = Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = stringResource(id = R.string.ResetPassword),
                    color = Color.White
                )
            }
        }
}

//------------------------------------------------------------------
//------------------------------------------------------------------
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ForgetPasswordPagePreview()
{
    ForgetPasswordPage(navController = rememberNavController())
}
//---------------------------------------------------------------
//------------------------------------------------------------------
