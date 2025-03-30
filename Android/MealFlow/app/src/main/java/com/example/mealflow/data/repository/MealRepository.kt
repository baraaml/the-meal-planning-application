package com.example.mealflow.data.repository

import android.content.Context
import android.util.Log
import com.example.mealflow.data.model.Meal
import com.example.mealflow.data.model.MealResponse
import com.example.mealflow.network.ApiClient
import com.example.mealflow.network.ApiMeal
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess

class MealRepository(private val context: Context) {
    private val apiMeal = ApiMeal()
    private var cachedMeals: List<Meal> = emptyList()

    suspend fun getRecommendedMeals(): Result<List<Meal>> {
        Log.d("MealRepository", "Fetching recommended meals")
        return apiMeal.getRecommendedMeals().map { response ->
            // Cache the meals for future use
            val meals = response.data
            cachedMeals = meals
            meals
        }
    }

    suspend fun searchMeals(query: String): Result<List<Meal>> {
        Log.d("MealRepository", "Searching meals with query: $query")

        // If we have cached meals, filter them first
        if (cachedMeals.isNotEmpty()) {
            val filteredMeals = cachedMeals.filter { meal ->
                meal.name.contains(query, ignoreCase = true) ||
                        meal.description.contains(query, ignoreCase = true) ||
                        meal.tags.any { it.contains(query, ignoreCase = true) } ||
                        meal.ingredients.any { it.name.contains(query, ignoreCase = true) }
            }

            return Result.success(filteredMeals)
        }

        // If no cached meals, fetch from API
        return try {
            // Ideally, we would call a search API endpoint here
            // For now, we'll fetch all meals and filter client-side
            val result = apiMeal.getRecommendedMeals()

            if (result.isSuccess) {
                val allMeals = result.getOrNull()?.data ?: emptyList()
                cachedMeals = allMeals

                val filteredMeals = allMeals.filter { meal ->
                    meal.name.contains(query, ignoreCase = true) ||
                            meal.description.contains(query, ignoreCase = true) ||
                            meal.tags.any { it.contains(query, ignoreCase = true) } ||
                            meal.ingredients.any { it.name.contains(query, ignoreCase = true) }
                }

                Result.success(filteredMeals)
            } else {
                result.map { response ->
                    response.data.filter { meal ->
                        meal.name.contains(query, ignoreCase = true) ||
                                meal.description.contains(query, ignoreCase = true) ||
                                meal.tags.any { it.contains(query, ignoreCase = true) } ||
                                meal.ingredients.any { it.name.contains(query, ignoreCase = true) }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MealRepository", "Error searching meals: ${e.message}")
            Result.failure(e)
        }
    }
}