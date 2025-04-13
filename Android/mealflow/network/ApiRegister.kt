package com.example.mealflow.network

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RegisterRequest(val username: String, val email: String, val password: String)

@Serializable
data class RegisterResponse(val success: Boolean, val message: String)

fun registerUser(
    context: Context,
    username: String,
    email: String,
    password: String,
    navController: NavController,
    onError: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    CoroutineScope(Dispatchers.IO).launch {
//        val client = HttpClient(CIO) {
//            install(ContentNegotiation) {
//                json(Json { ignoreUnknownKeys = true })
//            }
//        }
        val client = ApiClient.client
        val url = ApiClient.Endpoints.REGISTER

        try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(username, email, password))
            }

            val responseBodyText = response.bodyAsText()

            val message = try {
                Json.decodeFromString<RegisterResponse>(responseBodyText).message
            } catch (e: Exception) {
                Regex("\"message\"\\s*:\\s*\"(.*?)\"").find(responseBodyText)?.groupValues?.get(1)
                    ?: "An unknown error occurred."
            }

            Log.d("RegisterUser", "Response Status: ${response.status}")
            Log.d("RegisterUser", "Message: $message")

            withContext(Dispatchers.Main) {
                // Display the Snackbar here after getting the message
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )

                if (response.status == HttpStatusCode.OK || message == "User account created successfully. Please check your email for the verification code.") {
                    Log.d("RegisterUser", "✅ Successful registration: Navigate to OtpPage")
                    navController.navigate("Otp Page") {
                        popUpTo("Register Page") { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    Log.e("RegisterUser", "❌ Registration failed:$message")
                    onError(message)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "Error occurred while connecting to the server.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}
