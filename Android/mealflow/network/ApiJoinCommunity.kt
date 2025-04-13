package com.example.mealflow.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.example.mealflow.database.token.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ----------------------- JoinCommunityRequest ---------------------------
//@Serializable
//data class JoinCommunityRequest(val accessToken: String)

// ----------------------- JoinCommunityResponse ---------------------------
@Serializable
data class JoinCommunityResponse(
    val success: Boolean,
    val message: String,
    val community: JoinCommunity? = null
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
        Log.d("URL", "📩Send request: URL = $url")
        val tokenManager = TokenManager(context)
        val accessToken = tokenManager.getAccessToken()

        try {
            Log.d("API", "📩Send request: Token = $accessToken, idCommunity=$idCommunity")

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
                    Log.d("API", "✅ Successful login, go to the next page")
                } else {
                    Log.e("API", "❌ Login failed:${responseBody.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, responseBody.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("API", "❌ Exception during order execution:${e.localizedMessage}")
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "An error occurred while connecting to the server.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}


