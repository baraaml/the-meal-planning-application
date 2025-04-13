package com.example.mealflow.network

import android.util.Log
import com.example.mealflow.data.model.MealResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiMeal {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private val baseUrl = "https://mealflow.ddns.net/api/v1"

    suspend fun getRecommendedMeals(limit: Int = 10, recommendationType: String? = null): Result<MealResponse> {
        return try {
            val response = client.get("$baseUrl/meal/recommended") {
                parameter("limit", limit)
                if (recommendationType != null) {
                    parameter("recommendation_type", recommendationType)
                }
            }

            if (response.status.isSuccess()) {
                val mealResponse = response.body<MealResponse>()
                Result.success(mealResponse)
            } else {
                Result.failure(Exception("Failed to fetch recommended meals: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("ApiMeal", "Error fetching recommended meals: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getTrendingMeals(limit: Int = 10, timeWindow: String = "week"): Result<MealResponse> {
        return try {
            val response = client.get("$baseUrl/meal/trending") {
                parameter("limit", limit)
                parameter("time_window", timeWindow)
            }

            if (response.status.isSuccess()) {
                val mealResponse = response.body<MealResponse>()
                Result.success(mealResponse)
            } else {
                Result.failure(Exception("Failed to fetch trending meals: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("ApiMeal", "Error fetching trending meals: ${e.message}", e)
            Result.failure(e)
        }
    }
}