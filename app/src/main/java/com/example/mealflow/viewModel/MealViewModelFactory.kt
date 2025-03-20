package com.example.mealflow.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mealflow.data.repository.MealRepository
import com.example.mealflow.network.ApiMeal

class MealViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealViewModel::class.java)) {
            // Create ApiMeal instance first
            val apiMeal = ApiMeal()
            // Then pass it to the repository
            val repository = MealRepository(apiMeal)
            return MealViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}