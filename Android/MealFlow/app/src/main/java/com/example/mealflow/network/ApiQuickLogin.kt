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
import androidx.navigation.NavController
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
data class QuickLoginRequest(val refreshToken: String)

// ----------------------- JoinCommunityResponse ---------------------------
@Serializable
data class QuickLoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserData? = null
)

@Serializable
data class UserData(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

fun quickLoginApi(
    refreshToken: String,
    context: Context,
    navController: NavController
    ) {
    CoroutineScope(Dispatchers.IO).launch {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
        val url = "https://mealflow.ddns.net/api/v1/users/quick-login"
        Log.d("URL", "📩 إرسال الطلب: email=$url")
        val tokenManager = TokenManager(context)
        try {
            Log.d("API", "📩 إرسال الطلب: refreshToken=$refreshToken")

            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(QuickLoginRequest(refreshToken))
            }

            val responseBody = response.body<QuickLoginResponse>()

            withContext(Dispatchers.Main) {
                Log.d("API", "response : ${responseBody.message}")
                Log.d("API", "response : $responseBody")
                if (response.status.isSuccess() && responseBody.success) {
                    Log.d("API", "✅ تسجيل دخول ناجح، الانتقال إلى الصفحة التالية")
                    Log.d("API", "data : ${responseBody.data}")
                    responseBody.data?.let {
                        tokenManager.saveTokens(it.accessToken, it.refreshToken)
                    }
                    navController.navigate("Home Page")
                } else {
                    Log.e("API", "❌ فشل تسجيل الدخول: ${responseBody.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, responseBody.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("API", "❌ استثناء أثناء تنفيذ الطلب: ${e.localizedMessage}")
        }
    }
}


