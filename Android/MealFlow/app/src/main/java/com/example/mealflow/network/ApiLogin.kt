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

// ----------------------- LoginRequest ---------------------------
@Serializable
data class LoginRequest(val email: String, val password: String)

// ----------------------- LoginResponse ---------------------------
@Serializable
data class LoginResponse(val success: Boolean, val message: String,val data: Data? = null)


@Serializable
data class Data(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

fun loginApi(
    context: Context,
    email: String,
    password: String,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    CoroutineScope(Dispatchers.IO).launch {
        val client = ApiClient.client
        val url = ApiClient.Endpoints.LOGIN
        val tokenManager = TokenManager(context)
        try {
            Log.d("API", "📩 Send Request: email=$email, password=$password")

            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            val responseBody = response.body<LoginResponse>()

            withContext(Dispatchers.Main) {
                // Display the message independently of the navigation
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar(
                        message = responseBody.message,
                        duration = SnackbarDuration.Short
                    )
                }
                // Go directly without waiting
                if (response.status.isSuccess() && responseBody.success) {
                    Log.d("API", "✅ Successful login, go to the next page")
                    // Extract and save the token only without `TokenEntity`
                    responseBody.data?.let {
                        tokenManager.saveTokens(it.accessToken, it.refreshToken)
                    }
                    Log.d("accessToken", "accessToken: ${tokenManager.getAccessToken()}")
                    Log.d("refreshToken", "refreshToken: ${tokenManager.getRefreshToken()}")
                    navController.navigate("Home Page") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                } else {
                    Log.e("API", "❌ login failed: ${responseBody.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("API", "❌ Exception during order execution: ${e.localizedMessage}")
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "Error occurred while connecting to the server.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}