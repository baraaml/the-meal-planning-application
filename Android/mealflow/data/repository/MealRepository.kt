package com.example.mealflow.data.repository

import android.content.Context
import android.util.Log
import com.example.mealflow.data.model.Meal
import com.example.mealflow.data.model.MealResponse
import com.example.mealflow.network.ApiMeal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MealRepository(private val context: Context) {
    private val apiMeal = ApiMeal()
    private var cachedRecommendedMeals: List<Meal> = emptyList()
    private var cachedTrendingMeals: List<Meal> = emptyList()
    private var cachedCollaborativeMeals: List<Meal> = emptyList()

    suspend fun getRecommendedMeals(): Result<List<Meal>> {
        Log.d("MealRepository", "Fetching recommended meals (content-based)")
        return try {
            val response = withContext(Dispatchers.IO) {
                apiMeal.getRecommendedMeals(recommendationType = "content")
            }

            if (response.isSuccess) {
                val meals = response.getOrNull()?.data ?: emptyList()
                cachedRecommendedMeals = meals
                Result.success(meals)
            } else {
                Result.failure(Exception("Failed to fetch recommended meals: ${response.exceptionOrNull()?.message}"))
            }
        } catch (e: Exception) {
            Log.e("MealRepository", "Error fetching recommended meals: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getTrendingMeals(timeWindow: String = "week", limit: Int = 10): Result<List<Meal>> {
        Log.d("MealRepository", "Fetching trending meals (time window: $timeWindow)")
        return try {
            val response = withContext(Dispatchers.IO) {
                apiMeal.getTrendingMeals(timeWindow = timeWindow, limit = limit)
            }

            if (response.isSuccess) {
                val meals = response.getOrNull()?.data ?: emptyList()
                cachedTrendingMeals = meals
                Result.success(meals)
            } else {
                Result.failure(Exception("Failed to fetch trending meals: ${response.exceptionOrNull()?.message}"))
            }
        } catch (e: Exception) {
            Log.e("MealRepository", "Error fetching trending meals: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getCollaborativeRecommendations(): Result<List<Meal>> {
        Log.d("MealRepository", "Fetching collaborative recommendations")
        return try {
            val response = withContext(Dispatchers.IO) {
                apiMeal.getRecommendedMeals(recommendationType = "collaborative")
            }

            if (response.isSuccess) {
                val meals = response.getOrNull()?.data ?: emptyList()
                cachedCollaborativeMeals = meals
                Result.success(meals)
            } else {
                Result.failure(Exception("Failed to fetch collaborative recommendations: ${response.exceptionOrNull()?.message}"))
            }
        } catch (e: Exception) {
            Log.e("MealRepository", "Error fetching collaborative recommendations: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun searchMeals(query: String): Result<List<Meal>> {
        Log.d("MealRepository", "Searching meals with query: $query")

        // Combine all cached meals for searching
        val allCachedMeals = cachedRecommendedMeals + cachedTrendingMeals + cachedCollaborativeMeals

        if (allCachedMeals.isNotEmpty()) {
            val filteredMeals = allCachedMeals.filter { meal ->
                meal.name.contains(query, ignoreCase = true) ||
                        meal.description.contains(query, ignoreCase = true) ||
                        meal.tags.any { it.contains(query, ignoreCase = true) } ||
                        meal.ingredients.any { it.name.contains(query, ignoreCase = true) }
            }

            return Result.success(filteredMeals)
        }

        // If no cached meals, fetch recommended meals and filter
        return try {
            val response = withContext(Dispatchers.IO) {
                apiMeal.getRecommendedMeals()
            }

            if (response.isSuccess) {
                val allMeals = response.getOrNull()?.data ?: emptyList()

                val filteredMeals = allMeals.filter { meal ->
                    meal.name.contains(query, ignoreCase = true) ||
                            meal.description.contains(query, ignoreCase = true) ||
                            meal.tags.any { it.contains(query, ignoreCase = true) } ||
                            meal.ingredients.any { it.name.contains(query, ignoreCase = true) }
                }

                Result.success(filteredMeals)
            } else {
                Result.failure(Exception("Failed to search meals: ${response.exceptionOrNull()?.message}"))
            }
        } catch (e: Exception) {
            Log.e("MealRepository", "Error searching meals: ${e.message}", e)
            Result.failure(e)
        }
    }
}