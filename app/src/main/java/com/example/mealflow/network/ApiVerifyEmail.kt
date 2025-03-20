package com.example.mealflow.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import io.ktor.client.request.accept
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
data class OtpRequest(val otp: String, val email: String)

@Serializable
data class OtpResponse(val success: Boolean, val message: String, val data: AuthData? = null)

@Serializable
data class AuthData(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

@Serializable
data class User(
    val id: String,
    val email: String,
    val isVerified: Boolean
)

fun verifyEmail(context: Context, otp: String, email: String, navController: NavController) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            Log.d("API", "🔹 إرسال طلب التحقق: ${ApiClient.Endpoints.VERIFY_EMAIL}")
            Log.d("API", "📩 بيانات الطلب: otp=$otp, email=$email")

            val response: HttpResponse = ApiClient.client.post(ApiClient.Endpoints.VERIFY_EMAIL) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(OtpRequest(otp, email))
            }

            val responseText = response.bodyAsText()
            Log.d("API", "🔹 استجابة الخادم: $responseText")
            
            withContext(Dispatchers.Main) {
                if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Accepted) {
                    val responseBody = Json.decodeFromString<OtpResponse>(responseText)
                    if (responseBody.success) {
                        Log.d("API", "✅ التحقق ناجح! الانتقال إلى الصفحة التالية")
                        navController.navigate("Test Page")
                    } else {
                        Toast.makeText(context, responseBody.message, Toast.LENGTH_LONG).show()
                        Log.e("API", "❌ خطأ في التحقق: ${responseBody.message}")
                    }
                } else {
                    Log.e("API", "⚠️ استجابة غير متوقعة: ${response.status}")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "فشل التحقق: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("API", "❌ استثناء أثناء تنفيذ الطلب: ${e.message}")
            }
        }
        // No need to close the client as it's shared
    }
}