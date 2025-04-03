// Location: network/ApiMeal.kt
package com.example.mealflow.network

import com.example.mealflow.data.model.MealResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess

class ApiMeal {
    private val client = ApiClient.client

    suspend fun getRecommendedMeals(): Result<MealResponse> {
        return try {
            val response = client.get(ApiClient.Endpoints.RECOMMENDED_MEALS)

            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("API error: ${response.status.value} - ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}