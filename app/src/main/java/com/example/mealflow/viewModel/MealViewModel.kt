package com.example.mealflow.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealflow.data.model.Meal
import com.example.mealflow.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class MealViewModel(
    application: Application,
    private val repository: MealRepository
) : AndroidViewModel(application) {

    // State for meals
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    // State for loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Flag to prevent duplicate fetch requests
    private val isInitialFetchDone = AtomicBoolean(false)

    init {
        // We'll fetch meals when the ViewModel is created if we're not waiting for login
        fetchRecommendedMeals()
    }

    // Call this method after login to fetch recommended meals
    fun fetchRecommendedMeals() {
        // Only fetch if we haven't already or if we're explicitly refreshing
        if (!isInitialFetchDone.get() || _meals.value.isEmpty()) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null

                try {
                    Log.d("MealViewModel", "Fetching recommended meals from repository")

                    // Fetch data from repository
                    val result = repository.getRecommendedMeals()

                    if (result.isSuccess) {
                        val mealList = result.getOrNull() ?: emptyList()
                        if (mealList.isNotEmpty()) {
                            Log.d("MealViewModel", "Successfully fetched ${mealList.size} meals")
                            _meals.value = mealList
                            isInitialFetchDone.set(true)
                        } else {
                            Log.d("MealViewModel", "Meal list was empty from API")
                            _meals.value = emptyList()
                            _errorMessage.value = "No meals available from the server."
                        }
                    } else {
                        val exception = result.exceptionOrNull() ?: Exception("Unknown error")
                        Log.e("MealViewModel", "Error fetching meals: ${exception.message}", exception)

                        // Keep current meals or set to empty if none
                        if (_meals.value.isEmpty()) {
                            _errorMessage.value = "Could not connect to the server. Pull to refresh."
                        } else {
                            _errorMessage.value = "Could not refresh meals. Using cached data."
                        }

                        // Log the stack trace for debugging
                        exception.printStackTrace()
                    }
                } catch (e: Exception) {
                    Log.e("MealViewModel", "Exception when fetching meals: ${e.message}", e)

                    _errorMessage.value = "Unexpected error occurred. Please try again."

                    e.printStackTrace()
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    // Function to force a refresh of the meal data
    fun refreshMeals() {
        isInitialFetchDone.set(false) // Reset the flag to force a fresh fetch
        fetchRecommendedMeals()
    }

    // Function to search meals by query
    fun searchMeals(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                if (query.isEmpty()) {
                    // If empty query, restore original meals list
                    if (_meals.value.isEmpty()) {
                        // If we don't have real data yet, fetch it
                        fetchRecommendedMeals()
                    } else {
                        // Use the meals we already fetched
                        _isLoading.value = false
                    }
                    return@launch
                }

                // Call repository for search function
                val result = repository.searchMeals(query)

                if (result.isSuccess) {
                    val matchingMeals = result.getOrNull() ?: emptyList()
                    if (matchingMeals.isNotEmpty()) {
                        _meals.value = matchingMeals
                    } else {
                        // We found no matches
                        _meals.value = emptyList()
                        _errorMessage.value = "No meals found matching '$query'"
                    }
                } else {
                    val exception = result.exceptionOrNull() ?: Exception("Unknown error")
                    Log.e("MealViewModel", "Error searching meals: ${exception.message}")

                    // Fallback to local filtering of whatever data we have
                    val currentMeals = _meals.value
                    if (currentMeals.isNotEmpty()) {
                        val filteredMeals = currentMeals.filter { meal ->
                            meal.name.contains(query, ignoreCase = true) ||
                                    meal.description.contains(query, ignoreCase = true) ||
                                    meal.tags.any { it.contains(query, ignoreCase = true) }
                        }

                        _meals.value = filteredMeals
                        if (filteredMeals.isEmpty()) {
                            _errorMessage.value = "No meals found matching '$query'"
                        } else {
                            _errorMessage.value = "Using local search results (server unavailable)"
                        }
                    } else {
                        _errorMessage.value = "Cannot search - no meal data available. Try refreshing first."
                    }
                }
            } catch (e: Exception) {
                Log.e("MealViewModel", "Exception during search: ${e.message}")
                _errorMessage.value = "Search failed: ${e.message}"

                // Keep the current meal list
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Function to reset meal list to the original fetched data
    fun resetMealList() {
        // Simply clear the search by setting empty query
        searchMeals("")
    }
}