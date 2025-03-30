package com.example.mealflow.network

import android.content.Context
import com.example.mealflow.database.token.TokenManager
import com.example.mealflow.database.token.installAuthInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

class ApiClientToken(context: Context) {
    companion object {
        // Base URL for all API endpoints
        private const val BASE_URL = "https://mealflow.ddns.net/api/v1"

        // API endpoint paths
        object Endpoints {
            const val LOGIN = "$BASE_URL/users/login"
            const val REGISTER = "$BASE_URL/users/register"
            const val VERIFY_EMAIL = "$BASE_URL/users/verify-email"
            const val FORGOT_PASSWORD = "$BASE_URL/users/forgot-password"
            const val RESET_PASSWORD = "$BASE_URL/users/reset-password"
            const val RESET_OTP = "$BASE_URL/users/resend-verification"

            // Meal endpoints
            const val RECOMMENDED_MEALS = "$BASE_URL/meal/recommended"
        }
    }

    private val tokenManager = TokenManager(context)

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }

        installAuthInterceptor(tokenManager) // ربط التوكنات بكل طلب
    }
}
