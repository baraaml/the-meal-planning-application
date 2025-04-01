package com.example.mealflow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.R
import com.example.mealflow.buttons.DynamicButton
import com.example.mealflow.network.resetOtpApi
import com.example.mealflow.network.verifyEmail
import com.example.mealflow.viewModel.RegisterViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent


// ----------------------- Otp Page ---------------------------
// ----------------------- Function to display text with otp input ---------------------------
@Composable
fun OtpPage(navController: NavController,viewModel: RegisterViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            // ----------------------- Header Text -----------------------------
            Text(
                text = stringResource(id = R.string.HeaderOtp),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            // ---------------- Function contains OTP input and verify email button (send OTP) -----------------
            OTPVerificationScreen(navController, viewModel)
        }
    }
}

// ----------------------- Function to preview OtpPage ---------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewOtpPage()
{
    val testViewModel: RegisterViewModel = viewModel()
    OtpPage(navController = rememberNavController(),testViewModel)
}


@Composable
fun OTPInputField(
    otpLength: Int = 6,
    onOtpEntered: (String) -> Unit
) {
    // ----------------------- Variables ---------------------------
    var otpValues by remember { mutableStateOf(List(otpLength) { "" }) }
    val focusRequesters = List(otpLength) { remember { FocusRequester() } }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        otpValues.forEachIndexed { index, value ->
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    // Handle paste in first field
                    if (index == 0 && newValue.length > 1 && newValue.all { it.isDigit() }) {
                        // Handle paste operation
                        val pastedOtp = newValue.take(otpLength)
                        val newOtpValues = List(otpLength) { i ->
                            if (i < pastedOtp.length) pastedOtp[i].toString() else ""
                        }
                        otpValues = newOtpValues

                        // Focus on the appropriate field after pasting
                        if (pastedOtp.length < otpLength) {
                            focusRequesters[pastedOtp.length].requestFocus()
                        }

                        // Notify if we have a complete OTP
                        if (pastedOtp.length == otpLength) {
                            onOtpEntered(pastedOtp)
                        }
                    } else if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                        // Normal single digit input handling
                        val newOtpValues = otpValues.toMutableList()
                        newOtpValues[index] = newValue
                        otpValues = newOtpValues

                        // Auto-advance focus when entering a digit
                        if (newValue.isNotEmpty() && index < otpLength - 1) {
                            focusRequesters[index + 1].requestFocus()
                        }

                        // Notify if we have a complete OTP
                        if (otpValues.joinToString("").length == otpLength) {
                            onOtpEntered(otpValues.joinToString(""))
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .width(50.dp)
                    .height(55.dp)
                    .focusRequester(focusRequesters[index])
                    .padding(4.dp)
                    // Add key event handler for backspace navigation
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Backspace && value.isEmpty() && index > 0) {
                            // Move focus to previous field when backspace is pressed on an empty field
                            focusRequesters[index - 1].requestFocus()
                            true
                        } else {
                            false
                        }
                    },
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }
    }
}
@Composable
fun OTPVerificationScreen(navController: NavController, viewModel: RegisterViewModel) {
    var otp by remember { mutableStateOf("") }
    val context = LocalContext.current
    val email by viewModel.email.observeAsState("")
    var timer by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            delay(1000L)
            timer--
        } else {
            canResend = true
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(id = R.string.check_email), fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OTPInputField(onOtpEntered = { otp = it })
        Spacer(modifier = Modifier.height(16.dp))
        DynamicButton(
            onClick = { verifyEmail(context, otp, email, navController) },
            textOnButton = stringResource(id = R.string.Verification),
            buttonWidthDynamic = 200
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (canResend) {
            Text(
                text = "Resend OTP",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                modifier = Modifier.clickable {
                    timer = 60
                    canResend = false
                    // You can add any page reset operation here
                    resetOtpApi(email, navController, snackbarHostState,coroutineScope)
                }
            )
        } else {
            Text(text = "Resend after: $timer seconds", fontSize = 14.sp)
        }
    }
}