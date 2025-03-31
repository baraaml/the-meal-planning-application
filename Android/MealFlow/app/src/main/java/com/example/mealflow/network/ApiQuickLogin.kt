package com.example.mealflow.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.mealflow.database.token.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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

// ----------------------- QuickLoginRequest ---------------------------
@Serializable
data class QuickLoginRequest(val refreshToken: String)

// ----------------------- QuickLoginResponse ---------------------------
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
        val url = ApiClient.Endpoints.QUICK_LOGIN
        Log.d("URL", "📩Send request: URL = $url")
        val tokenManager = TokenManager(context)
        try {
            Log.d("API", "📩Send request: refreshToken = $refreshToken")

            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(QuickLoginRequest(refreshToken))
            }

            val responseBody = response.body<QuickLoginResponse>()

            withContext(Dispatchers.Main) {
                Log.d("API", "response : ${responseBody.message}")
                Log.d("API", "response : $responseBody")
                if (response.status.isSuccess() && responseBody.success) {
                    Log.d("API", "✅ Successful login, go to the next page")
                    Log.d("API", "data : ${responseBody.data}")
                    responseBody.data?.let {
                        tokenManager.saveTokens(it.accessToken, it.refreshToken)
                    }
                    navController.navigate("Home Page")
                } else {
                    Log.e("API", "❌ Login failed : ${responseBody.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, responseBody.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("API", "❌ Exception during order execution:${e.localizedMessage}")
        }
    }
}


