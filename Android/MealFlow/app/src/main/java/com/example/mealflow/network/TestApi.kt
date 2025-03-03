//package com.example.mealflow.network
//
//import kotlinx.coroutines.runBlocking
//import retrofit2.Retrofit
//import retrofit2.converter.scalars.ScalarsConverterFactory
//import retrofit2.http.GET
//
//// 1️⃣ تعريف API لجلب كل المستخدمين
//interface ApiService {
//    @GET("/api/v1/users/") // تأكد أن الـ API يدعم جلب جميع المستخدمين
//    suspend fun getAllUsers(): String
//}
//
//// 2️⃣ إنشاء Retrofit Instance مع ScalarsConverterFactory
//val retrofit = Retrofit.Builder()
//    .baseUrl("https://someday-to-do-list-v1.vercel.app") // استبدله بالرابط الصحيح
//    .addConverterFactory(ScalarsConverterFactory.create()) // إرجاع JSON كنص
//    .build()
//
//// 3️⃣ إنشاء كائن من API
//val api = retrofit.create(ApiService::class.java)
//
//// 4️⃣ تشغيل التطبيق وجلب البيانات
//fun main() = runBlocking {
//    try {
//        val response = api.getAllUsers() // جلب كل المستخدمين
//        println(response) // طباعة الـ JSON كما هو
//    } catch (e: Exception) {
//        println("Error fetching users: ${e.message}")
//    }
//}
//---------------------------------------------------------------------------------------------
//import kotlinx.coroutines.runBlocking
//import retrofit2.Retrofit
//import retrofit2.converter.scalars.ScalarsConverterFactory
//import retrofit2.http.Body
//import retrofit2.http.POST
//
//interface ApiService {
//    @POST("/api/v1/users/") // تعديل الطلب ليكون POST
//    suspend fun createUser(@Body userData: String): String // إرسال بيانات المستخدم
//}
//// 2️⃣ إنشاء Retrofit Instance مع ScalarsConverterFactory
//val retrofit = Retrofit.Builder()
//    .baseUrl("https://mealflow.ddns.net/api/v1/users/register") // استبدله بالرابط الصحيح
//    .addConverterFactory(ScalarsConverterFactory.create()) // إرجاع JSON كنص
//    .build()
//
//// 3️⃣ إنشاء كائن من API
//val api = retrofit.create(ApiService::class.java)
//
//fun main() = runBlocking {
//    try {
//        val newUserData = """{"username": "John Doe", "email": "john@example.com"}""" // بيانات المستخدم الجديد
//        val response = api.createUser(newUserData) // إرسال البيانات
//        println(response) // طباعة الاستجابة
//    } catch (e: Exception) {
//        println("Error creating user: ${e.message}")
//    }
//}

//------------------------------------------------------------------------------------------

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
//fun registerUser1(username: String, email: String, password: String) = runBlocking {
//    val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(Json { ignoreUnknownKeys = true })
//        }
//    }
//
//    val url = "https://mealflow.ddns.net/api/v1/users/register"
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
//    registerUser1("abdelrahman", "abdoag163@gmail.com", "StrongPassword#123")
//}