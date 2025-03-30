//package com.example.mealflow.network
//
//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.plugins.contentnegotiation.*
//import kotlinx.serialization.json.Json
//import io.ktor.serialization.kotlinx.json.*
//
//object ApiClient {
//    val client: HttpClient by lazy {
//        HttpClient(CIO) {
//            install(ContentNegotiation) {
//                json(Json {
//                    ignoreUnknownKeys = true
//                    isLenient = true
//                })
//            }
//        }
//    }
//}
// Location: network/ApiClient.kt
package com.example.mealflow.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Singleton object that provides a shared HTTP client for all API requests.
 * This ensures consistent configuration and efficient resource usage.
 */
object ApiClient {
    // Base URL for all API endpoints
    const val BASE_URL = "https://mealflow.ddns.net/api/v1"

    // Shared HTTP client instance
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                // Add any other JSON configuration here
            })
        }

        // You can add additional client configurations here as needed:
        // - Timeout settings
        // - Logging
        // - Error handling
        // - Authentication interceptors
    }

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
