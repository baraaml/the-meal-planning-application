package com.example.mealflow.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mealflow.database.token.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ----------------------- JoinCommunityRequest ---------------------------
@Serializable
data class JoinCommunityRequest(val accessToken: String)

// ----------------------- JoinCommunityResponse ---------------------------
@Serializable
data class JoinCommunityResponse(
    val success: Boolean,
    val message: String,
    val community: JoinCommunity? = null // 👈 الآن community يمكن أن يكون مفقودًا
)

@Serializable
data class JoinCommunity(
    val communityId: String,
    val userId: String,
    val role: String,
    val joinedAt: String,
    val leftAt: String?,
    val isPending: Boolean
)


fun joinCommunityApi(
    idCommunity: String,
    context: Context,
    snackbarHostState: SnackbarHostState
) {
    CoroutineScope(Dispatchers.IO).launch {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
        val url = "https://mealflow.ddns.net/api/v1/community/$idCommunity/join"
        Log.d("URL", "📩 إرسال الطلب: email=$url")
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
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, responseBody.message, Toast.LENGTH_SHORT).show()
                    }
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


