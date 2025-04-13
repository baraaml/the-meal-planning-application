package com.example.mealflow.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealflow.data.model.Meal
import com.example.mealflow.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealViewModel(application: Application, repository1: MealRepository) : AndroidViewModel(application) {
    private val repository = MealRepository(application)

    // State flows for each type of meal list
    private val _recommendedMeals = MutableStateFlow<List<Meal>>(emptyList())
    val recommendedMeals = _recommendedMeals.asStateFlow()

    private val _trendingMeals = MutableStateFlow<List<Meal>>(emptyList())
    val trendingMeals = _trendingMeals.asStateFlow()

    private val _collaborativeMeals = MutableStateFlow<List<Meal>>(emptyList())
    val collaborativeMeals = _collaborativeMeals.asStateFlow()

    // Generic meals list (combined from various sources) for compatibility with existing code
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals = _meals.asStateFlow()

    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Search results
    private val _searchResults = MutableStateFlow<List<Meal>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    init {
        fetchAllMealTypes()
    }

    fun fetchAllMealTypes() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Fetch all three types of meals in parallel
                val recommendedDeferred = viewModelScope.launch {
                    fetchRecommendedMeals()
                }

                val trendingDeferred = viewModelScope.launch {
                    fetchTrendingMeals()
                }

                val collaborativeDeferred = viewModelScope.launch {
                    fetchCollaborativeRecommendations()
                }

                // Wait for all to complete
                recommendedDeferred.join()
                trendingDeferred.join()
                collaborativeDeferred.join()

                // Update the generic meals list with all meals (for compatibility)
                _meals.value = _recommendedMeals.value + _trendingMeals.value + _collaborativeMeals.value

                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error fetching all meal types: ${e.message}", e)
                _errorMessage.value = "Failed to load meals: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun fetchRecommendedMeals() {
        viewModelScope.launch {
            try {
                val result = repository.getRecommendedMeals()
                if (result.isSuccess) {
                    _recommendedMeals.value = result.getOrNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load recommended meals"
                }
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error fetching recommended meals: ${e.message}", e)
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun fetchTrendingMeals() {
        viewModelScope.launch {
            try {
                val result = repository.getTrendingMeals()
                if (result.isSuccess) {
                    _trendingMeals.value = result.getOrNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load trending meals"
                }
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error fetching trending meals: ${e.message}", e)
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun fetchCollaborativeRecommendations() {
        viewModelScope.launch {
            try {
                val result = repository.getCollaborativeRecommendations()
                if (result.isSuccess) {
                    _collaborativeMeals.value = result.getOrNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load recommendations"
                }
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error fetching collaborative recommendations: ${e.message}", e)
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun searchMeals(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.searchMeals(query)
                if (result.isSuccess) {
                    _searchResults.value = result.getOrNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Search failed: ${result.exceptionOrNull()?.message}"
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error searching meals: ${e.message}", e)
                _errorMessage.value = "Search error: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshMeals() {
        fetchAllMealTypes()
    }
}