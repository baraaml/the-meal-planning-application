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
data class LogoutRequest(val refreshToken: String)

// ----------------------- LoginResponse ---------------------------
@Serializable
data class LogoutResponse(val success: Boolean, val message: String,val data: Data? = null)


fun logoutApi(
    context: Context, // إضافة `context` هنا
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    CoroutineScope(Dispatchers.IO).launch {
        val client = ApiClient.client
        val url = "https://mealflow.ddns.net/api/v1/users/logout"
        val tokenManager = TokenManager(context)
        val refreshToken = tokenManager.getRefreshToken()
        try {
            Log.d("API", "📩 إرسال الطلب: email=$refreshToken")
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(LogoutRequest(refreshToken.toString()))
            }

            val responseBody = response.body<LogoutResponse>()

            withContext(Dispatchers.Main) {
                // عرض الرسالة بشكل غير معتمد على عملية الانتقال
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar(
                        message = responseBody.message,
                        duration = SnackbarDuration.Short
                    )
                }
                // الانتقال مباشرة بدون انتظار
                if (response.status.isSuccess() && responseBody.success) {
                    Log.d("API", "✅ تسجيل دخول ناجح، الانتقال إلى الصفحة التالية")
                    Log.d("accessToken", "accessToken: ${tokenManager.getAccessToken()}")
                    Log.d("refreshToken", "refreshToken: ${tokenManager.getRefreshToken()}")
                    tokenManager.clearAccessToken()
                    navController.navigate("Login Page")
                } else {
                    Log.e("API", "❌ فشل تسجيل الدخول: ${responseBody.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("API", "❌ استثناء أثناء تنفيذ الطلب: ${e.localizedMessage}")
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "حدث خطأ أثناء الاتصال بالسيرفر",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}