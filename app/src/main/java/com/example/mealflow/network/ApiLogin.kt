package com.example.mealflow.network

import android.util.Log
import androidx.navigation.NavController
import com.example.mealflow.viewModel.LoginViewModel
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

// ----------------------- LoginRequest ---------------------------
@Serializable
data class LoginRequest(val email: String, val password: String)

// ----------------------- LoginResponse ---------------------------
@Serializable
data class LoginResponse(val success: Boolean, val message: String, val data: Data? = null)

@Serializable
data class Data(
    val accessToken: String,
    val refreshToken: String,
    val user: ApiUser
)

@Serializable
data class ApiUser(
    val id: String,
    val email: String,
    val isVerified: Boolean
)

suspend fun loginApi(email: String, password: String, navController: NavController, viewModel: LoginViewModel) {
    try {
        Log.d("API", "📩 Sending request: email=$email, password=$password")

        val response: HttpResponse = ApiClient.client.post(ApiClient.Endpoints.LOGIN) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }

        if (response.status.isSuccess()) {
            val responseBody = response.body<LoginResponse>()

            withContext(Dispatchers.Main) {
                viewModel.setLoginMessage(responseBody.message)

                if (responseBody.success) {
                    Log.d("API", "✅ Login successful, navigating to Home Screen")

                    // Instead of directly navigating, trigger the navigation through ViewModel
                    viewModel.navigateToHomeScreen()
                } else {
                    Log.e("API", "❌ Login failed: ${responseBody.message}")
                }
            }
        } else {
            val errorText = response.bodyAsText()
            Log.e("API", "⚠️ Server error (${response.status}): $errorText")

            withContext(Dispatchers.Main) {
                viewModel.setLoginMessage("Login failed: Server error (${response.status})")
            }
        }
    } catch (e: Exception) {
        Log.e("API", "❌ Exception while executing request: ${e.localizedMessage}")

        withContext(Dispatchers.Main) {
            viewModel.setLoginMessage("Login failed: ${e.localizedMessage}")
        }
    }
}