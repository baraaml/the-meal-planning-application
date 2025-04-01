package com.example.mealflow.network

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

// ----------------------- ForgetPasswordRequest ---------------------------
@Serializable
data class ForgetPasswordRequest(val email: String)

// ----------------------- ForgetPasswordResponse ---------------------------
@Serializable
data class ForgetPasswordResponse(val success: Boolean, val message: String,val data: PasswordData? = null)


@Serializable
data class PasswordData(
    val token: String
)

fun forgetPasswordApi(
    email: String,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    CoroutineScope(Dispatchers.IO).launch { // ✅ Run the request in a separate thread (Background Thread)

        val client = ApiClient.client
        val url = ApiClient.Endpoints.FORGOT_PASSWORD

        try {
            Log.d("API", "📩Send the request: email= $email")
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(ForgetPasswordRequest(email))
            }
            val responseBody = response.body<ForgetPasswordResponse>()
            CoroutineScope(Dispatchers.Main).launch {
                snackbarHostState.showSnackbar(
                    message = responseBody.message,
                    duration = SnackbarDuration.Short
                )
            }
            if (response.status.isSuccess()) {
                withContext(Dispatchers.Main) { // ✅ Update UI within the Main Thread
                    if (responseBody.success) {
                        Log.d("API", "✅ Successful login, go to the next page")
                        navController.navigate("Test Page")
                    } else {
                        Log.e("API", "❌ Login failed:${responseBody.message}")
                    }
                }
            } else {
                val errorText = response.bodyAsText()
                Log.e("API", "⚠ Server error (${response.status}): $errorText")
            }
        } catch (e: Exception) {
            Log.e("API", "❌ Exception during order execution:${e.localizedMessage}")
        }
    }
}