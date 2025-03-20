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
data class ForgetPasswordRequest(val email: String)

@Serializable
data class ForgetPasswordResponse(val success: Boolean, val message: String, val data: PasswordData? = null)

@Serializable
data class PasswordData(
    val token: String,
)

fun forgetPasswordApi(email: String, navController: NavController, viewModel: ForgetPasswordViewModel) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            Log.d("API", "📩 إرسال الطلب: email=$email")

            val response: HttpResponse = ApiClient.client.post(ApiClient.Endpoints.FORGOT_PASSWORD) {
                contentType(ContentType.Application.Json)
                setBody(ForgetPasswordRequest(email))
            }

            if (response.status.isSuccess()) {
                val responseBody = response.body<ForgetPasswordResponse>()

                withContext(Dispatchers.Main) {
                    if (responseBody.success) {
                        Log.d("API", "✅ تسجيل دخول ناجح، الانتقال إلى الصفحة التالية")
                        navController.navigate("Reset Password Page")
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
        }
        // No need to close the client as it's shared
    }
}