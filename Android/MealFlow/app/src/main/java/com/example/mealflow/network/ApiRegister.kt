import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.mealflow.random.ErrorPopupRegister
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.util.Log

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
    onError: (String) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

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
                if (response.status == HttpStatusCode.OK) {
                    Log.d("RegisterUser", "✅ تسجيل ناجح: التنقل إلى OtpPage")
                    navController.navigate("OtpPage") {
                        popUpTo("register") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                else if(message == "User account created successfully. Please check your email for the verification code.")
                {
                    Log.d("RegisterUser", "✅ تسجيل ناجح: التنقل إلى OtpPage")
                    navController.navigate("Otp Page") {
                        popUpTo("Register Page") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                else {
                    Log.e("RegisterUser", "❌ تسجيل فشل: $message")
                    onError(message) // ✅ يظهر فقط عند الفشل
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                val errorMessage = "حدث خطأ أثناء التسجيل: ${e.message}"
                Log.e("RegisterUser", "❌ خطأ استثنائي: $errorMessage", e)
                onError(errorMessage) // ✅ استدعاء `onError` فقط عند حدوث خطأ
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        } finally {
            client.close()
        }
    }
}
