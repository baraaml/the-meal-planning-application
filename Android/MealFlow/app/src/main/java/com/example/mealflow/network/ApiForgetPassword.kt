package com.example.mealflow.network

import android.util.Log
import androidx.navigation.NavController
import com.example.mealflow.viewModel.ForgetPasswordViewModel
import com.example.mealflow.viewModel.LoginViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// ----------------------- LoginRequest ---------------------------
@Serializable
data class ForgetPasswordRequest(val email: String)

// ----------------------- LoginResponse ---------------------------
@Serializable
data class ForgetPasswordResponse(val success: Boolean, val message: String,val data: PasswordData? = null)


@Serializable
data class PasswordData(
    val token: String,
)


fun forgetPasswordApi(email: String, navController: NavController, viewModel: ForgetPasswordViewModel) {
    CoroutineScope(Dispatchers.IO).launch { // ✅ تشغيل الطلب في خيط منفصل (Background Thread)
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val url = "https://mealflow.ddns.net/api/v1/users/forgot-password"

        try {
            Log.d("API", "📩 إرسال الطلب: email=$email")

            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(ForgetPasswordRequest(email))
            }

            if (response.status.isSuccess()) {
                val responseBody = response.body<ForgetPasswordResponse>()

                withContext(Dispatchers.Main) { // ✅ تحديث UI داخل الـ Main Thread
                    //viewModel.setLoginMessage(responseBody.message)

                    if (responseBody.success) {
                        Log.d("API", "✅ تسجيل دخول ناجح، الانتقال إلى الصفحة التالية")
                        navController.navigate("Test Page")
                    } else {
                        Log.e("API", "❌ فشل تسجيل الدخول: ${responseBody.message}")
                    }
                }
            } else {
                val errorText = response.bodyAsText()
                Log.e("API", "⚠️ خطأ من السيرفر (${response.status}): $errorText")
            }
        } catch (e: Exception) {
            Log.e("API", "❌ استثناء أثناء تنفيذ الطلب: ${e.localizedMessage}")
        } finally {
            client.close()
        }
    }
}
