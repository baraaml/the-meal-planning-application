//package com.example.mealflow.network
//
//import android.content.Context
//import android.net.Uri
//import android.util.Log
//import androidx.compose.material3.SnackbarDuration
//import androidx.compose.material3.SnackbarHostState
//import androidx.navigation.NavController
//import com.example.mealflow.viewModel.LoginViewModel
//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.engine.cio.CIO
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.request.forms.MultiPartFormDataContent
//import io.ktor.client.request.forms.formData
//import io.ktor.client.request.get
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.client.statement.HttpResponse
//import io.ktor.client.statement.bodyAsText
//import io.ktor.http.ContentType
//import io.ktor.http.Headers
//import io.ktor.http.HttpStatusCode
//import io.ktor.http.contentType
//import io.ktor.http.isSuccess
//import io.ktor.serialization.kotlinx.json.json
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.Contextual
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//
//// ----------------------- LoginRequest ---------------------------
//@Serializable
//data class CreateCommunityRequest(
//    val name: String,
//    val description: String,
//    val recipeCreationPermission:String,
//    val categories:List<String>,
//    @Contextual val image: Uri?
//)
//
//// ----------------------- LoginResponse ---------------------------
//@Serializable
//data class CreateCommunityResponse(
//    val success: Boolean,
//    val message: String,
//    val community: Community
//)
//@Serializable
//data class Community(
//    val id: String,
//    val name: String,
//    val description: String,
//    val ownerId: String,
//    val image: String,
//    val privacy: String,
//    val recipeCreationPermission: String,
//    val createdAt: String,
//    val updatedAt: String,
//    val categories: List<Category>,
//    val members: List<Member>,
//    val owner: OwnerCommunity
//)
//
//@Serializable
//data class Category(
//    val id: String,
//    val name: String,
//    val parentId: String
//)
//
//@Serializable
//data class Member(
//    val id: String,
//    val communityId: String,
//    val userId: String,
//    val role: String,
//    val joinedAt: String,
//    val leftAt: String?,
//    val isPending: Boolean,
//    val user: User
//)
//
//@Serializable
//data class UserCreateCommunity(
//    val id: String,
//    val name: String?,
//    val username: String
//)
//
//@Serializable
//data class OwnerCommunity(
//    val id: String,
//    val name: String?,
//    val username: String
//)
//
//fun createCommunityApi(
//    context: Context,
//    name: String,
//    description: String,
//    recipeCreationPermission: String,
//    categories: List<String>,
//    imageUri: Uri?,
//    navController: NavController,
//    snackbarHostState: SnackbarHostState
//) {
//    CoroutineScope(Dispatchers.IO).launch {
//        val client = ApiClient.client ?: return@launch
//        val url = "https://mealflow.ddns.net/api/v1/community"
//
//        try {
//            Log.d("API", "📩 إرسال الطلب: name=$name, description=$description")
//
//            val response: HttpResponse = client.post(url) {
//                contentType(ContentType.MultiPart.FormData)
//                setBody(MultiPartFormDataContent(formData {
//                    append("name", name)
//                    append("description", description)
//                    append("recipeCreationPermission", recipeCreationPermission)
//                    categories.forEach { category ->
//                        append("categories[]", category)
//                    }
//
//                    // معالجة الصورة كملف
//                    imageUri?.let {
//                        val inputStream = context.contentResolver.openInputStream(it)
//                        val bytes = inputStream?.readBytes() ?: ByteArray(0)
//
//                        append("image", bytes, Headers.build {
//                            append("Content-Disposition", "form-data; name=\"image\"; filename=\"community_image.jpg\"")
//                            append("Content-Type", "image/jpeg")
//                        })
//                    }
//                }))
//            }
//
//            val responseBody = Json { ignoreUnknownKeys = true }
//                .decodeFromString<CreateCommunityResponse>(response.bodyAsText())
//
//            withContext(Dispatchers.Main) {
//                snackbarHostState.showSnackbar(
//                    message = responseBody.message,
//                    duration = SnackbarDuration.Short
//                )
//
//                if (response.status.isSuccess() && responseBody.success) {
//                    navController.navigate("Home Page") {
//                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
//                    }
//                } else {
//                    Log.e("API", "❌ فشل إنشاء المجتمع: ${responseBody.message}")
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("API", "❌ خطأ أثناء إرسال الطلب: ${e.localizedMessage}", e)
//            withContext(Dispatchers.Main) {
//                snackbarHostState.showSnackbar(
//                    message = "حدث خطأ أثناء الاتصال بالسيرفر: ${e.localizedMessage}",
//                    duration = SnackbarDuration.Short
//                )
//            }
//        }
//    }
//}



package com.example.mealflow.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mealflow.viewModel.LoginViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import io.ktor.client.request.*
import io.ktor.client.request.forms.InputProvider
import io.ktor.http.*
import io.ktor.util.InternalAPI
import io.ktor.utils.io.streams.asInput
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import java.nio.channels.ByteChannel
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import java.io.ByteArrayInputStream

// ----------------------- LoginRequest ---------------------------
@Serializable
data class CreateCommunityRequest(
    val name: String,
    val description: String,
    val recipeCreationPermission: String,
    val categories: List<String>,
    @Contextual val image: Uri?
)

// ----------------------- LoginResponse ---------------------------
@Serializable
data class CreateCommunityResponse(
    val success: Boolean,
    val message: String,
    val community: Community
)
@Entity(tableName = "community_table")
@Serializable
data class Community(
//    val id: String,
    @PrimaryKey val id: Int,  // ✅ تم إضافة @PrimaryKey
    val name: String,
    val description: String,
    val ownerId: String,
    val image: String?,  // 👈 اجعلها nullable
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
    val parentId: String? = null // اجعل الحقل اختياريًا
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

object JsonConfig {
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
}

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

            val url = "https://mealflow.ddns.net/api/v1/community"

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

// ------------------ Test Function ------------------
fun testCreateCommunityApi(
    context: Context,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    createCommunityApi(
        context = context,
        name = "Test Community",
        description = "This is a test community",
        recipeCreationPermission = "all",
        accessToken = "sfdsfdsd",
        categories = listOf("Category1", "Category2"),
        imageUri = null,  // لو عايز تختبر بصورة حقيقية حط الـ Uri هنا
        navController = navController,
        snackbarHostState = snackbarHostState
    )
}