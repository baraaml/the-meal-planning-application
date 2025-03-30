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
data class LoginRequest(val email: String, val password: String)

// ----------------------- LoginResponse ---------------------------
@Serializable
data class LoginResponse(val success: Boolean, val message: String,val data: Data? = null)


@Serializable
data class Data(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

@Serializable
data class ApiUser(
    val id: String,
    val email: String,
    val isVerified: Boolean
)

fun loginApi(
    context: Context, // إضافة `context` هنا
    email: String,
    password: String,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    CoroutineScope(Dispatchers.IO).launch {
        val client = ApiClient.client
        val url = "https://mealflow.ddns.net/api/v1/users/login"
        val tokenManager = TokenManager(context)
        try {
            Log.d("API", "📩 إرسال الطلب: email=$email, password=$password")

            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            val responseBody = response.body<LoginResponse>()

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
                    // استخراج وحفظ التوكن فقط بدون `TokenEntity`
                    responseBody.data?.let {
                        tokenManager.saveTokens(it.accessToken, it.refreshToken)
                    }
                    Log.d("accessToken", "accessToken: ${tokenManager.getAccessToken()}")
                    Log.d("refreshToken", "refreshToken: ${tokenManager.getRefreshToken()}")
                    navController.navigate("Home Page") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
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
//fun loginApi(
//    email: String,
//    password: String,
//    navController: NavController,
//    snackbarHostState: LoginViewModel
//) {
//    CoroutineScope(Dispatchers.IO).launch {
//        val client = HttpClient(CIO) {
//            install(ContentNegotiation) {
//                json(Json {
//                    ignoreUnknownKeys = true
//                    isLenient = true
//                })
//            }
//        }
//
//        val url = "https://mealflow.ddns.net/api/v1/users/login"
//
//        try {
//            Log.d("API", "📩 إرسال الطلب: email=$email, password=$password")
//
//            val response: HttpResponse = client.post(url) {
//                contentType(ContentType.Application.Json)
//                setBody(LoginRequest(email, password))
//            }
//
//            val responseBody = response.body<LoginResponse>()
//
//            if (response.status.isSuccess() && responseBody.success) {
//                Log.d("API", "✅ تسجيل دخول ناجح، الانتقال إلى الصفحة التالية")
//
//                // التنقل للصفحة التالية
//                withContext(Dispatchers.Main) {
//                    navController.navigate("Home Page") {
//                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
//                    }
//                }
//            }
//
//            // عرض الرسالة سواء كان النجاح أو الفشل
//            withContext(Dispatchers.Main) {
//                snackbarHostState.showSnackbar(
//                    message = responseBody.message,
//                    duration = SnackbarDuration.Short
//                )
//            }
//        } catch (e: Exception) {
//            Log.e("API", "❌ استثناء أثناء تنفيذ الطلب: ${e.localizedMessage}")
//
//            withContext(Dispatchers.Main) {
//                snackbarHostState.showSnackbar(
//                    message = "حدث خطأ أثناء الاتصال بالسيرفر",
//                    duration = SnackbarDuration.Short
//                )
//            }
//        } finally {
//            client.close()
//        }
//    }
//}
