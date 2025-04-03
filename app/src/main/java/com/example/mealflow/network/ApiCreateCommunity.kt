package com.example.mealflow.network

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

// ----------------------- ApiCreateCommunityRequest ---------------------------
//@Serializable
//data class CreateCommunityRequest(
//    val name: String,
//    val description: String,
//    val recipeCreationPermission: String,
//    val categories: List<String>,
//    @Contextual val image: Uri?
//)

// ----------------------- ApiCreateCommunityResponse ---------------------------
@Serializable
data class CreateCommunityResponse(
    val success: Boolean,
    val message: String,
    val community: Community
)
@Entity(tableName = "community_table")
@Serializable
data class Community(
    @PrimaryKey val id: String,  // ✅ @PrimaryKey has been added
    val name: String,
    val description: String,
    val ownerId: String,
    val image: String?,
    val privacy: String,
    val recipeCreationPermission: String,
    val createdAt: String,
    val updatedAt: String,
    val categories: List<Category>,
    val members: List<Member>,
    val owner: OwnerCommunity
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val parentId: String? = null
)

@Serializable
data class Member(
    val role: String,
    val joinedAt: String,
    val isPending: Boolean? = null,
    val user: UserCreateCommunity
)

@Serializable
data class UserCreateCommunity(
    val id: String,
    val name: String?,
    val username: String
)

@Serializable
data class OwnerCommunity(
    val id: String,
    val name: String?,
    val username: String
)

//@OptIn(ExperimentalSerializationApi::class)
fun createCommunityApi(
    context: Context,
    name: String,
    description: String,
    recipeCreationPermission: String,
    accessToken: String,
    categories: List<String>,
    imageUri: Uri?,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    // Validate inputs
    if (name.isBlank() || description.isBlank() || accessToken.isBlank()) {
        CoroutineScope(Dispatchers.Main).launch {
            snackbarHostState.showSnackbar(
                message = "Please fill in all required fields",
                duration = SnackbarDuration.Short
            )
        }
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
                install(Logging) {
                    level = LogLevel.ALL
                }
            }

            val url = ApiClient.Endpoints.CREATE_COMMUNITY

            // Prepare image for upload
            val imageFile: File? = imageUri?.let { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val tempFile = File.createTempFile("community_image", ".jpg", context.cacheDir)
                    tempFile.outputStream().use { fileOut ->
                        inputStream?.copyTo(fileOut)
                    }
                    tempFile
                } catch (e: Exception) {
                    Log.e("ImageUpload", "Error processing image: ${e.localizedMessage}")
                    null
                }
            }

            // Prepare multipart form data
            val response = client.post(url) {
                headers {
                    append("Authorization", "Bearer $accessToken")
                }
                contentType(ContentType.MultiPart.FormData)

                setBody(MultiPartFormDataContent(
                    formData {
                        append("name", name)
                        append("description", description)
                        append("recipeCreationPermission", recipeCreationPermission)
                        append("categories", Json.encodeToString(categories))

                        imageFile?.let { file ->
                            append("image", file.readBytes(),
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                                }
                            )
                        }
                    }
                ))
            }

            // Process response
            val responseBody = response.bodyAsText()
            Log.d("CommunityCreation", "Response: $responseBody")

            val apiResponse = try {
                Json { ignoreUnknownKeys = true }.decodeFromString<CreateCommunityResponse>(responseBody)
            } catch (e: Exception) {
                Log.e("CommunityCreation", "Parsing error: ${e.localizedMessage}")
                null
            }

            // Handle response on Main thread
            withContext(Dispatchers.Main) {
                if (apiResponse?.success == true) {
                    snackbarHostState.showSnackbar(
                        message = "Community created successfully!",
                        duration = SnackbarDuration.Short
                    )
                    // Safe navigation
                    navController.navigate("Home Page") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                } else {
                    snackbarHostState.showSnackbar(
                        message = apiResponse?.message ?: "Failed to create community",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "Error: ${e.localizedMessage}",
                    duration = SnackbarDuration.Short
                )
            }
            Log.e("CommunityCreation", "Error: ${e.localizedMessage}", e)
        }
    }
}