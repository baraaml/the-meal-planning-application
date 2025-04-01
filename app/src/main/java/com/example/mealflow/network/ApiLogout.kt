package com.example.mealflow.network

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import com.example.mealflow.database.token.TokenManager
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

// ----------------------- LogoutRequest ---------------------------
@Serializable
data class LogoutRequest(val refreshToken: String)

// ----------------------- LogoutResponse ---------------------------
@Serializable
data class LogoutResponse(val success: Boolean, val message: String,val data: Data? = null)


fun logoutApi(
    context: Context,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    CoroutineScope(Dispatchers.IO).launch {
        val client = ApiClient.client
        val url = ApiClient.Endpoints.LOGOUT
        val tokenManager = TokenManager(context)
        val refreshToken = tokenManager.getRefreshToken()
        try {
            Log.d("API", "📩Send request: refreshToken = $refreshToken")
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(LogoutRequest(refreshToken.toString()))
            }

            val responseBody = response.body<LogoutResponse>()

            withContext(Dispatchers.Main) {
                // Display the message independently of the transition
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar(
                        message = responseBody.message,
                        duration = SnackbarDuration.Short
                    )
                }
                // Go directly without waiting
                if (response.status.isSuccess() && responseBody.success) {
                    Log.d("API", "✅ Successful login, go to the next page")
                    Log.d("accessToken", "accessToken: ${tokenManager.getAccessToken()}")
                    Log.d("refreshToken", "refreshToken: ${tokenManager.getRefreshToken()}")
                    tokenManager.clearAccessToken()
                    navController.navigate("Login Page")
                } else {
                    Log.e("API", "❌ Login failed: ${responseBody.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("API", "❌ Exception during order execution:${e.localizedMessage}")
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "Error occurred while connecting to the server.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}