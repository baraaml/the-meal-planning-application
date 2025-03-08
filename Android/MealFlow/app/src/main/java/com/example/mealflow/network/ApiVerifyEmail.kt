
package com.example.mealflow.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OtpRequest(val otp: String, val email: String)

@Serializable
data class OtpResponse(val success: Boolean, val message: String,  val data: AuthData? = null)

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

fun verifyEmail(context: Context, otp: String, email: String, navController: NavController) = runBlocking {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val url = "https://mealflow.ddns.net/api/v1/users/verify-email"

    try {
        Log.d("API", "🔹 إرسال طلب التحقق: $url")
        Log.d("API", "📩 بيانات الطلب: otp=$otp, email=$email")

        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json) // تأكيد قبول JSON كاستجابة
            setBody(OtpRequest(otp, email))
        }

        val responseText = response.bodyAsText()
        Log.d("API", "🔹 استجابة الخادم: $responseText")
        //response.status == HttpStatusCode.OK
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
    } catch (e: Exception) {
        Toast.makeText(context, "فشل التحقق: ${e.message}", Toast.LENGTH_LONG).show()
        Log.e("API", "❌ استثناء أثناء تنفيذ الطلب: ${e.message}")
    } finally {
        client.close()
    }
}
