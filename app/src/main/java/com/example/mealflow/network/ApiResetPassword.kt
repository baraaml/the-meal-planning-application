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
    CoroutineScope(Dispatchers.IO).launch {
        try {
            Log.d("API", "📩 إرسال الطلب: token=$token")
            Log.d("API", "📩 إرسال الطلب: password=$password")
            
            val response: HttpResponse = ApiClient.client.post(ApiClient.Endpoints.RESET_PASSWORD) {
                contentType(ContentType.Application.Json)
                setBody(ResetPasswordRequest(token, password))
            }

            if (response.status.isSuccess()) {
                val responseBody = response.body<ResetPasswordResponse>()

                withContext(Dispatchers.Main) {
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
        }
        // No need to close the client as it's shared
    }
}