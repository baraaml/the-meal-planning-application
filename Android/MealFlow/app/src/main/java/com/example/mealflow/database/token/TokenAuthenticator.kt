package com.example.mealflow.database.token

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class RefreshTokenResponse(val success: Boolean, val message: String, val data: TokenData?)

@Serializable
data class TokenData(val accessToken: String)

suspend fun refreshAccessToken(tokenManager: TokenManager): String? {
    val refreshToken = tokenManager.getRefreshToken() ?: return null

    val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    val response: HttpResponse = client.post("https://mealflow.ddns.net/api/v1/users/refresh-token") {
        contentType(ContentType.Application.Json)
        setBody(RefreshTokenRequest(refreshToken))
    }

    return if (response.status == HttpStatusCode.OK) {
        val jsonResponse = response.body<RefreshTokenResponse>()
        val newAccessToken = jsonResponse.data?.accessToken

        if (newAccessToken != null) {
            tokenManager.saveTokens(newAccessToken, refreshToken)
        } else {
            tokenManager.clearTokens() // إذا كان هناك خطأ، امسح التوكنات
        }
        newAccessToken
    } else {
        tokenManager.clearTokens() // إذا كان الـ Refresh Token غير صالح، احذف التوكنات
        null
    }
}

//suspend fun refreshAccessToken(tokenManager: TokenManager): String? {
//    val refreshToken = tokenManager.getRefreshToken() ?: return null
//
//    val client = HttpClient {
//        install(ContentNegotiation) {
//            json()
//        }
//    }
//
//    val response: HttpResponse = client.post("https://mealflow.ddns.net/api/v1/users/refresh-token") {
//        contentType(ContentType.Application.Json)
//        setBody(mapOf("refresh_token" to refreshToken))
//    }
//
//    return if (response.status == HttpStatusCode.OK) {
//        val json = response.body<Map<String, String>>()
//        val newAccessToken = json["access_token"]
//        val newRefreshToken = json["refresh_token"]
//
//        if (newAccessToken != null && newRefreshToken != null) {
//            tokenManager.saveTokens(newAccessToken, newRefreshToken)
//        }
//        newAccessToken
//    } else {
//        tokenManager.clearTokens() // إذا كان الـ Refresh Token غير صالح، احذف التوكنات
//        null
//    }
//}