package com.example.mealflow.data.repository

import android.content.Context
import com.example.mealflow.data.model.Meal

class MealRepository(private val apiMeal: Context) {
    // Removed the redundant initialization as we're now using the injected apiMeal

    suspend fun getRecommendedMeals(): Result<List<Meal>> {
        return apiMeal.getRecommendedMeals().map { response ->
            response.data
        }
    }
}