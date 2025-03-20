package com.example.mealflow.network

import android.content.Context
import android.util.Log
import android.widget.Toast
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
    onError: (String) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: HttpResponse = ApiClient.client.post(ApiClient.Endpoints.REGISTER) {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(username, email, password))
            }

            val responseBodyText = response.bodyAsText()
            val message = try {
                Json.decodeFromString<RegisterResponse>(responseBodyText).message
            } catch (e: Exception) {
                Regex("\"message\"\\s*:\\s*\"(.*?)\"").find(responseBodyText)?.groupValues?.get(1)
                    ?: "حدث خطأ غير معروف"
            }

            Log.d("RegisterUser", "Response Status: ${response.status}")
            Log.d("RegisterUser", "Message: $message")

            withContext(Dispatchers.Main) {
                if (response.status == HttpStatusCode.OK) {
                    Log.d("RegisterUser", "✅ تسجيل ناجح: التنقل إلى OtpPage")
                    navController.navigate("OtpPage") {
                        popUpTo("register") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                else if(message == "User account created successfully. Please check your email for the verification code.")
                {
                    Log.d("RegisterUser", "✅ تسجيل ناجح: التنقل إلى OtpPage")
                    navController.navigate("Otp Page") {
                        popUpTo("Register Page") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                else {
                    Log.e("RegisterUser", "❌ تسجيل فشل: $message")
                    onError(message)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                val errorMessage = "حدث خطأ أثناء التسجيل: ${e.message}"
                Log.e("RegisterUser", "❌ خطأ استثنائي: $errorMessage", e)
                onError(errorMessage)
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
        // No need to close the client as it's shared
    }
}