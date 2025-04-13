package com.example.mealflow.buttons

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

//---------------------- ApiVerifyEmail -----------------------
//// دالة تسجيل المستخدم بدون إرجاع قيمة
//fun  verifyEmail(context: Context, otp: String, email: String, navController: NavController) = runBlocking {
//    val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(Json { ignoreUnknownKeys = true })
//        }
//    }
//
//    val url = "https://mealflow.ddns.net/api/v1/users/verify-email"
//
//    try {
//        val response: HttpResponse = client.post(url) {
//            contentType(ContentType.Application.Json)
//            setBody(OtpRequest(otp, email))
//        }
//        val responseBody = Json.decodeFromString<OtpResponse>(response.bodyAsText())
//
//        if (responseBody.success) {
//            println("Registration successful!")
//            navController.navigate("Test Page")
//        } else {
//            Toast.makeText(context, responseBody.message, Toast.LENGTH_LONG).show()
//            Log.e("API", "خطأ في جلب البيانات: ${responseBody.message}")
//        }
//    } catch (e: Exception) {
//        Toast.makeText(context, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
//        Log.e("API", "خطأ في جلب البيانات: ${e.message}")
//        println("otp : ${otp} i: ${email}")
//    } finally {
//        client.close()
//    }
//}
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
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
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
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
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------