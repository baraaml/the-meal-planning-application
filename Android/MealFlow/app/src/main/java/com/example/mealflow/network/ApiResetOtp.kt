package com.example.mealflow.network

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// ----------------------- ResetOtpRequest ---------------------------
@Serializable
data class ResetOtpRequest(val email: String)

// ----------------------- ResetOtpResponse ---------------------------
@Serializable
data class ResetOtpResponse(val success: Boolean, val message: String)

fun resetOtpApi(
    email: String,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        val client = ApiClient.client
        val url = ApiClient.Endpoints.RESET_OTP

        try {
            Log.d("API", "📩 Sending request: email=$email")

            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(ResetOtpRequest(email))
            }

            if (response.status.isSuccess()) {
                val responseBody = response.body<ResetOtpResponse>()
                Log.d("API", "✅ OTP resent successfully.")
                snackbarHostState.showSnackbar(
                    message = responseBody.message,
                    duration = SnackbarDuration.Short
                )

                if (responseBody.success) {
                    Log.d("API", "✅ OTP resent successfully.")
                } else {
                    Log.e("API", "❌ OTP resend failed: ${responseBody.message}")
                }
            } else {
                Log.e("API", "❌ Server error: ${response.status.description}")
                snackbarHostState.showSnackbar(
                    message = "Failed to resend OTP. Please try again.",
                    duration = SnackbarDuration.Short
                )
            }
        } catch (e: Exception) {
            Log.e("API", "❌ Exception during request: ${e.localizedMessage}")
            snackbarHostState.showSnackbar(
                message = "Error connecting to the server.",
                duration = SnackbarDuration.Short
            )
        }
    }
}

