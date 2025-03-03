package com.example.mealflow.network

import android.util.Log
import androidx.navigation.NavController
import com.example.mealflow.viewModel.LoginViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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
    val user: ApiUser
)

@Serializable
data class ApiUser(
    val id: String,
    val eamil: String,
    val isVerified: Boolean
)

fun loginApi(email: String, password: String, navController: NavController, viewModel: LoginViewModel) {
    CoroutineScope(Dispatchers.IO).launch { // ✅ تشغيل الطلب في خيط منفصل (Background Thread)
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val url = "https://mealflow.ddns.net/api/v1/users/login"

        try {
            Log.d("API", "📩 إرسال الطلب: email=$email, password=$password")

            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            if (response.status.isSuccess()) {
                val responseBody = response.body<LoginResponse>()

                withContext(Dispatchers.Main) { // ✅ تحديث UI داخل الـ Main Thread
                    viewModel.setLoginMessage(responseBody.message)

                    if (responseBody.success) {
                        Log.d("API", "✅ تسجيل دخول ناجح، الانتقال إلى الصفحة التالية")
                        navController.navigate("Test Page")
                    } else {
                        Log.e("API", "❌ فشل تسجيل الدخول: ${responseBody.message}")
                    }
                }
            } else {
                val errorText = response.bodyAsText()
                Log.e("API", "⚠️ خطأ من السيرفر (${response.status}): $errorText")
            }
        } catch (e: Exception) {
            Log.e("API", "❌ استثناء أثناء تنفيذ الطلب: ${e.localizedMessage}")
        } finally {
            client.close()
        }
    }
}

// ----------------------- LoginApi ---------------------------
//fun loginApi(email: String, password: String, navController: NavController,viewModel: LoginViewModel) = runBlocking {
//    val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(Json { ignoreUnknownKeys = true })
//        }
//    }
//
//    val url = "https://mealflow.ddns.net/api/v1/users/login"
//
//    try {
//        Log.d("API", "📩 بيانات الطلب: password=$password, email=$email")
//        val response: HttpResponse = client.post(url) {
//            contentType(ContentType.Application.Json)
//            setBody(LoginRequest(email,password))
//        }
//
//        val responseBody = Json.decodeFromString<LoginResponse>(response.bodyAsText())
//        val responseText = response.bodyAsText()
//
//        viewModel.setLoginMessage(responseBody.message)
//        Log.d("API", "🔹 استجابة الخادم: $responseBody")
//        Log.d("API", "🔹 استجابة الخادم: $responseText")
//        if (responseBody.message == "Login successful") {
//            println("Registration successful!")
//            navController.navigate("Test Page")
//        } else {
//            println(responseBody.message)
//            Log.e("API", "❌ خطأ في التحقق: ${responseBody.message}")
//        }
//        if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Accepted) {
//            if (responseBody.success) {
//                Log.d("API", "✅ التحقق ناجح! الانتقال إلى الصفحة التالية")
//                navController.navigate("Test Page")
//            } else {
//                Log.e("API", "❌ خطأ في التحقق: ${responseBody.message}")
//            }
//        } else {
//            Log.e("API", "⚠️ استجابة غير متوقعة: ${response.status}")
//        }
//    } catch (e: Exception) {
//        println("Registration failed: ${e.message}")
//        Log.e("API", "❌ استثناء أثناء تنفيذ الطلب: ${e.message}")
//        //navController.navigate("Test Page")
//    } finally {
//        client.close()
//    }
//}

