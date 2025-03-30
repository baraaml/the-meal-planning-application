package com.example.mealflow.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.R
import com.example.mealflow.buttons.BackButton
import com.example.mealflow.network.forgetPasswordApi
import com.example.mealflow.utils.Validator
import com.example.mealflow.viewModel.ForgetPasswordViewModel


// ----------------------- Forget Password Page ---------------------------
@Composable
fun ForgetPasswordPage(
    navController: NavController,
    viewModel: ForgetPasswordViewModel = viewModel()
)
{
    // ----------------------- Variables ---------------------------
    val email by viewModel.email.observeAsState("")
    val emailError = Validator.validateEmail(email)
    var isFocusedEmail by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.observeAsState(false)
    val shouldNavigate by viewModel.navigateToTestPage.observeAsState(false)

    // snackbarHostState and CoroutineScope
    val snackbarHostState = remember { SnackbarHostState() }
    //val coroutineScope = rememberCoroutineScope()
    Box{
        Column {
            // ----------------------- Back Button ---------------------------
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp), horizontalArrangement = Arrangement.Start
            ) {
                BackButton(
                    onClick = { navController.popBackStack() }
                )
            }
            // ----------------------- Forgotten Password Text -----------------------------
            Text(
                text = stringResource(id = R.string.ForgottenPassword),
                Modifier.padding(start = 20.dp, top = 50.dp, bottom = 20.dp),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.sf_pro_rounded_heavy)),
            )
            // ----------------------- InputFields ---------------------------
            // ---------------------------------------------------------------
            // Input field ------------------ Email -----------------------
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.updateEmail(it) },
                label = {
                    Text(
                        stringResource(id = R.string.EnterEmail),
                        fontFamily = FontFamily(Font(R.font.sf_reg_ita))
                    )
                },
                singleLine = true,
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (emailError != null) Color.Red else Color.Blue,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 20.dp, end = 20.dp)
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

            // Add this somewhere in your ForgetPasswordPage for testing
            Button(
                onClick = { navController.navigate("Test Page") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Test Navigation")
            }
            Button(
                onClick = {
                    if (emailError == null) {
                        forgetPasswordApi(email, navController,snackbarHostState)
                    }
                },
                Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 100.dp)
                    .align(alignment = Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                enabled = emailError == null && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.ResetPassword),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = FontFamily(Font(R.font.sfmed))
                    )
                }
            }
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
//------------------------------------------------------------------
//------------------------------------------------------------------