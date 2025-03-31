package com.example.mealflow.network

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import com.example.mealflow.viewModel.ForgetPasswordViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ----------------------- ResetPasswordRequest ---------------------------
@Serializable
data class ResetPasswordRequest(val token: String, val password: String)

// ----------------------- ResetPasswordResponse ---------------------------
@Serializable
data class ResetPasswordResponse(val success: Boolean,val error: String? = null, val message: String,val details: ResetData? = null)


@Serializable
data class ResetData(
    val token: String,
)


fun resetPasswordApi(
    token: String,
    password: String,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    CoroutineScope(Dispatchers.IO).launch { // ✅ Run the request in a separate thread (Background Thread)
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val url = ApiClient.Endpoints.RESET_PASSWORD

        try {
            Log.d("API", "Token:Submit application=$token")
            Log.d("API", "Password:Submit application=$password")
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(ResetPasswordRequest(token,password))
            }
            val responseBody = response.body<ResetPasswordResponse>()
            withContext(Dispatchers.Main) { // ✅ UI update within the Main Thread
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar(
                        message = responseBody.message,
                        duration = SnackbarDuration.Short
                    )
                }
                if (response.status.isSuccess()) {
                    if (responseBody.success) {
                        Log.d("API", "✅Successful login, go to the next page")
                        delay(1000)
                        navController.navigate("Login Page")
                    } else {
                        Log.e("API", "❌ login failed: ${responseBody.message}")
                    }
                }
                else {
                    val errorText = response.bodyAsText()
                    Log.e("API", "⚠️ Server error (${response.status}): $errorText")
                }
            }
        } catch (e: Exception) {
            Log.e("API", "❌ Exception during request execution: ${e.localizedMessage}")
        }
    }
}
