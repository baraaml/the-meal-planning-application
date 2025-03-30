package com.example.mealflow.network

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.example.mealflow.database.token.TokenManager
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

// ----------------------- JoinCommunityRequest ---------------------------
@Serializable
data class JoinGetCommunityMembersRequest(val accessToken: String)

// ----------------------- JoinCommunityResponse ---------------------------
@Serializable
data class JoinGetCommunityMembersResponse(
    val success: Boolean,
    val message: String,
)

fun joinGetCommunityMembersApi(
    idCommunity: String,
    context: Context,
    snackbarHostState: SnackbarHostState
) {
    CoroutineScope(Dispatchers.IO).launch {
        val client = ApiClient.client
        val url = "https://mealflow.ddns.net/api/v1/community/$idCommunity/members"
        Log.d("URL", "📩 إرسال الطلب: URL=$url")
        val tokenManager = TokenManager(context)
        val accessToken = tokenManager.getAccessToken()
        try {
            Log.d("API", "📩 إرسال الطلب: Token=$accessToken, idCommunity=$idCommunity")

            val response: HttpResponse = client.post(url) {
                header("Authorization", "Bearer $accessToken")
                header("Content-Type", ContentType.Application.Json.toString())
            }
            val responseBody = response.body<JoinCommunityResponse>()

            withContext(Dispatchers.Main) {
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar(
                        message = responseBody.message,
                        duration = SnackbarDuration.Short
                    )
                }
                Log.d("API", "response : ${responseBody.message}")
                Log.d("API", "response : $responseBody")
                if (response.status.isSuccess() && responseBody.success) {
                    Log.d("API", "✅ تسجيل دخول ناجح، الانتقال إلى الصفحة التالية")
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