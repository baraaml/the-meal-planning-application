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
        val token = tokenManager.getAccessToken() // جلب التوكن الصحيح
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
            Log.d("API Response", response.bodyAsText()) // طباعة نص الاستجابة
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



//fun getCommunity(
//    context: Context,
//    name: String,
//    description: String,
//    recipeCreationPermission: String,
//    accessToken: String,
//    categories: List<String>,
//    imageUri: Uri?,
//    navController: NavController,
//    snackbarHostState: SnackbarHostState
//) {
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val client = HttpClient(CIO) {
//                install(ContentNegotiation) {
//                    json(Json {
//                        ignoreUnknownKeys = true
//                        isLenient = true
//                    })
//                }
//                install(Logging) {
//                    level = LogLevel.ALL
//                }
//            }
//
//            val url = "https://mealflow.ddns.net/api/v1/community"
//
//            // Prepare multipart form data
//            val response = client.post(url) {
////                setBody()
//            }
//
//            // Process response
//            val responseBody = response.bodyAsText()
//            Log.d("CommunityCreation", "Response: $responseBody")
//
//            val apiResponse = try {
//                Json { ignoreUnknownKeys = true }.decodeFromString<CreateCommunityResponse>(responseBody)
//            } catch (e: Exception) {
//                Log.e("CommunityCreation", "Parsing error: ${e.localizedMessage}")
//                null
//            }
//
//            // Handle response on Main thread
//            withContext(Dispatchers.Main) {
//                if (apiResponse?.success == true) {
//                    snackbarHostState.showSnackbar(
//                        message = "Community created successfully!",
//                        duration = SnackbarDuration.Short
//                    )
//                    // Safe navigation
//                    navController.navigate("Home Page") {
//                        popUpTo(navController.graph.startDestinationId) {
//                            inclusive = true
//                        }
//                    }
//                } else {
//                    snackbarHostState.showSnackbar(
//                        message = apiResponse?.message ?: "Failed to create community",
//                        duration = SnackbarDuration.Short
//                    )
//                }
//            }
//        } catch (e: Exception) {
//            withContext(Dispatchers.Main) {
//                snackbarHostState.showSnackbar(
//                    message = "Error: ${e.localizedMessage}",
//                    duration = SnackbarDuration.Short
//                )
//            }
//        }
//    }
//}

