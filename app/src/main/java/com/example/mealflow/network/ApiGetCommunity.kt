package com.example.mealflow.network

import android.content.Context
import android.util.Log
import com.example.mealflow.database.community.GetCommunityEntity
import com.example.mealflow.database.token.TokenManager
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable


@Serializable
data class CommunityResponse(
    val success: Boolean,
    val count: Int,
    val communities: List<GetCommunityEntity>
)

class CommunityApiService(private val context: Context) {
    suspend fun fetchCommunities(): CommunityResponse {
        val tokenManager = TokenManager(context)
        val token = tokenManager.getAccessToken()
        Log.d("Token", "Token : $token")

        if (token.isNullOrEmpty()) {
            Log.e("Community", "Token is null or empty")
            return CommunityResponse(false, 0, emptyList())
        }
        Log.d("Community", "fetchCommunities() called")
        val client = ApiClient.client
        Log.d("Token", "Token : $token")
        return try {
            val response: HttpResponse = client.get("https://mealflow.ddns.net/api/v1/community") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            Log.d("API Response", response.bodyAsText())
            Log.d("response", "Response status: ${response.status}")

            if (!response.status.isSuccess()) {
                Log.e("Community", "Failed to fetch communities: ${response.status}")
                return CommunityResponse(false, 0, emptyList())
            }
            val responseBody = response.body<CommunityResponse>()
            Log.d("Community", "API Response: $responseBody")
            responseBody

        } catch (e: Exception) {
            Log.e("Community", "Error fetching communities", e)
            CommunityResponse(false, 0, emptyList())
        }
    }
}