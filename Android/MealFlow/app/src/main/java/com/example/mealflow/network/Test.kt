package com.example.mealflow.network

//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import kotlinx.coroutines.runBlocking
//
//fun main() = runBlocking {
//    val client = HttpClient(CIO)
//    val response: HttpResponse = client.get("https://jsonplaceholder.typicode.com/posts/1")
//
//    println(response.bodyAsText())
//    client.close()
//}

//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import kotlinx.coroutines.runBlocking
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//
//// نموذج البيانات (Data Model)
//@Serializable
//data class RegisterRequest(val username: String, val email: String, val password: String)
//
//// دالة لإنشاء الحساب
//fun registerUser(username: String, email: String, password: String) = runBlocking {
//    val client = HttpClient(CIO) // محرك الشبكة
//    val url = "https://mealflow.ddns.net/api/v1/register" // عدل هذا بالـ API الخاص بك
//
//    try {
//        val response: HttpResponse = client.post(url) {
//            contentType(ContentType.Application.Json)
//            setBody(RegisterRequest(username, email, password))
//        }
//
//        println("Response: ${response.status}")
//        println("Body: ${response.bodyAsText()}") // طباعة استجابة السيرفر
//    } catch (e: Exception) {
//        println("Error: ${e.message}")
//    } finally {
//        client.close()
//    }
//}
//
//// تجربة تسجيل مستخدم جديد
//fun main() {
//    registerUser("abdelrahman", "abdoag163@gmail.com", "StrongPassword123")
//}

//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import kotlinx.coroutines.runBlocking
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//
//
//// نموذج البيانات القابل للتسلسل
//@Serializable
//data class RegisterRequest(val username: String, val email: String, val password: String)
//
//// دالة تسجيل المستخدم
//fun registerUser(username: String, email: String, password: String) = runBlocking {
//    val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(Json { ignoreUnknownKeys = true })
//        }
//    }
//
//    val url = "https://mealflow.ddns.net/api/v1/register"
//
//    try {
//        val response: HttpResponse = client.post(url) {
//            contentType(ContentType.Application.Json)
//            setBody(RegisterRequest(username, email, password))
//        }
//
//        println("Response: ${response.status}")
//        println("Body: ${response.bodyAsText()}")
//    } catch (e: Exception) {
//        println("Error: ${e.message}")
//    } finally {
//        client.close()
//    }
//}
//
//// تشغيل البرنامج
//fun main() {
//    registerUser("abdelrahman", "abdoag163@gmail.com", "StrongPassword123")
//}
//import android.content.Context
//import android.widget.Toast
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.json
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//
//@Serializable
//data class RegisterRequest(val username: String, val email: String, val password: String)

//fun registerUser(username: String, email: String, password: String) = runBlocking {
//    val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(Json { ignoreUnknownKeys = true })
//        }
//    }
//
//    val url = "https://mealflow.ddns.net/api/v1/register"
//
//    try {
//        val response: HttpResponse = client.post(url) {
//            contentType(ContentType.Application.Json)
//            setBody(RegisterRequest(username, email, password))
//        }
//
//        println("Response: ${response.status}")
//        println("Body: ${response.body<String>()}")
//    } catch (e: Exception) {
//        println("Error: ${e.message}")
//
//    } finally {
//        client.close()
//    }
//}

//fun registerUser(context: Context, username: String, email: String, password: String) {
//    CoroutineScope(Dispatchers.IO).launch {
//        val client = HttpClient(CIO) {
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//        val url = "https://mealflow.ddns.net/api/v1/register"
//
//        try {
//            val response: HttpResponse = client.post(url) {
//                contentType(ContentType.Application.Json)
//                setBody(RegisterRequest(username, email, password))
//            }
//
//            withContext(Dispatchers.Main) {
//                Toast.makeText(context, "Response: ${response.status}", Toast.LENGTH_LONG).show()
//            }
//        } catch (e: Exception) {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
//            }
//        } finally {
//            client.close()
//        }
//    }
//}
//fun registerUser(context: Context, username: String, email: String, password: String, onSuccess: (Boolean) -> Unit) {
//    CoroutineScope(Dispatchers.IO).launch {
//        val client = HttpClient(CIO) {
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//        val url = "https://mealflow.ddns.net/api/v1/users/register"
//
//        try {
//            val response: HttpResponse = client.post(url) {
//                contentType(ContentType.Application.Json)
//                setBody(RegisterRequest(username, email, password))
//            }
//
//            withContext(Dispatchers.Main) {
//                if (response.status == HttpStatusCode.OK) {
//                    onSuccess(true)
//                } else {
//                    Toast.makeText(context, "Registration failed!", Toast.LENGTH_LONG).show()
//                    onSuccess(false)
//                }
//            }
//        } catch (e: Exception) {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
//                onSuccess(false)
//            }
//        } finally {
//            client.close()
//        }
//    }
//}

