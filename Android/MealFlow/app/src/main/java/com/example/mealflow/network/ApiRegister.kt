import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import com.example.mealflow.network.ApiClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RegisterRequest(val username: String, val email: String, val password: String)

@Serializable
data class RegisterResponse(val success: Boolean, val message: String)

fun registerUser(
    context: Context,
    username: String,
    email: String,
    password: String,
    navController: NavController,
    onError: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    CoroutineScope(Dispatchers.IO).launch {
//        val client = HttpClient(CIO) {
//            install(ContentNegotiation) {
//                json(Json { ignoreUnknownKeys = true })
//            }
//        }
        val client = ApiClient.client
        val url = "https://mealflow.ddns.net/api/v1/users/register"

        try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(username, email, password))
            }

            val responseBodyText = response.bodyAsText()

            val message = try {
                Json.decodeFromString<RegisterResponse>(responseBodyText).message
            } catch (e: Exception) {
                Regex("\"message\"\\s*:\\s*\"(.*?)\"").find(responseBodyText)?.groupValues?.get(1)
                    ?: "حدث خطأ غير معروف"
            }

            Log.d("RegisterUser", "Response Status: ${response.status}")
            Log.d("RegisterUser", "Message: $message")

            withContext(Dispatchers.Main) {
                // عرض الـ Snackbar هنا بعد الحصول على الرسالة
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )

                if (response.status == HttpStatusCode.OK || message == "User account created successfully. Please check your email for the verification code.") {
                    Log.d("RegisterUser", "✅ تسجيل ناجح: التنقل إلى OtpPage")
                    navController.navigate("Otp Page") {
                        popUpTo("Register Page") { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    Log.e("RegisterUser", "❌ تسجيل فشل: $message")
                    onError(message)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "حدث خطأ أثناء الاتصال بالسيرفر",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}
