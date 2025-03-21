package com.example.mealflow.network

import android.util.Log
import androidx.navigation.NavController
import com.example.mealflow.viewModel.ForgetPasswordViewModel
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

@Serializable
data class ResetPasswordRequest(val token: String, val password: String)

@Serializable
data class ResetPasswordResponse(val success: Boolean, val error: String, val message: String, val details: ResetData? = null)

@Serializable
data class ResetData(
    val token: String,
)
fun ResetPasswordApi(token: String, password: String, navController: NavController, viewModel: ForgetPasswordViewModel) {
    // Set loading state
    viewModel.setLoading(true)
    viewModel.setErrorMessage("") // Clear any previous errors

    CoroutineScope(Dispatchers.IO).launch {
        try {
            Log.d("API", "📩 Sending request: token=$token")
            // Don't log the actual password in production
            Log.d("API", "📩 Sending request with password")

            val response: HttpResponse = ApiClient.client.post(ApiClient.Endpoints.RESET_PASSWORD) {
                contentType(ContentType.Application.Json)
                setBody(ResetPasswordRequest(token, password))
            }

            withContext(Dispatchers.Main) {
                viewModel.setLoading(false)

                if (response.status.isSuccess()) {
                    val responseBody = response.body<ResetPasswordResponse>()

                    if (responseBody.success) {
                        Log.d("API", "Password reset successful ✅")
                        // Navigate to the home screen
                        // Use the actual route name from your Navigation Graph
                        navController.navigate("Home Page") {
                            // Clear the back stack so user can't go back to reset page
                            popUpTo(0) { inclusive = true }
                        }

                    } else {
                        Log.e("API", "Reset failed ❌\n${responseBody.message}")
                        viewModel.setErrorMessage(responseBody.message)
                    }
                } else {
                    val errorText = response.bodyAsText()
                    Log.e("API", "Server error ⚠️\n${response.status}\n $errorText")
                    viewModel.setErrorMessage("Server error. Please try again later.")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                viewModel.setLoading(false)
                Log.e("API", "Exception occurred ❌\n${e.localizedMessage}")
                viewModel.setErrorMessage("Connection error. Please check your internet connection.")
            }
        }
    }
}