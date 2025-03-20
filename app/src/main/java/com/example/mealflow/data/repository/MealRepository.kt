package com.example.mealflow.data.repository

import com.example.mealflow.data.model.Meal
import com.example.mealflow.network.ApiMeal

class MealRepository(private val apiMeal: ApiMeal) {
    // Removed the redundant initialization as we're now using the injected apiMeal

    suspend fun getRecommendedMeals(): Result<List<Meal>> {
        return apiMeal.getRecommendedMeals().map { response ->
            response.data
        }
    }
}