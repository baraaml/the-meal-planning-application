package com.example.mealflow.database.token

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking

fun HttpClientConfig<*>.installAuthInterceptor(tokenManager: TokenManager) {
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 1)
        exponentialDelay()

        modifyRequest { request ->
            val accessToken = tokenManager.getAccessToken()
            if (accessToken != null) {
                request.headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }

        retryIf { _, response ->
            if (response.status == HttpStatusCode.Unauthorized) {
                val newToken = runBlocking { refreshAccessToken(tokenManager) }
                newToken != null // Retry only if we get a new token
            } else {
                false
            }
        }
    }
}
