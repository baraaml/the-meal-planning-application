package com.example.mealflow.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mealflow.data.model.Meal
import com.example.mealflow.data.repository.MealRepository
import com.example.mealflow.network.ApiMeal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealViewModel(
    application: Application,
    private val repository: MealRepository
) : AndroidViewModel(application) {
    // StateFlow for repository-based data
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> get() = _meals

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _rawJsonResponse = MutableStateFlow<String?>(null)
    val rawJsonResponse: StateFlow<String?> get() = _rawJsonResponse

    // LiveData for API-based data (from original MealViewModel)
    private val apiMeal = ApiMeal()

    private val _recommendedMeals = MutableLiveData<List<Meal>>()
    val recommendedMeals: LiveData<List<Meal>> = _recommendedMeals

    private val _apiIsLoading = MutableLiveData<Boolean>()
    val apiIsLoading: LiveData<Boolean> = _apiIsLoading

    private val _apiError = MutableLiveData<String?>()
    val apiError: LiveData<String?> = _apiError

    init {
        fetchRecommendedMeals()
    }

    // Method using repository pattern
    fun fetchRecommendedMeals() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = repository.getRecommendedMeals()
                result.onSuccess { meals ->
                    Log.d("MealViewModel", "Successfully received ${meals.size} meals")
                    _meals.value = meals
                    if (meals.isEmpty()) {
                        _errorMessage.value = "API returned 0 meals. Check the API response format."
                    }
                }.onFailure { exception ->
                    Log.e("MealViewModel", "Error fetching meals", exception)
                    val errorMsg = "Unable to load meals: ${exception.message ?: "Unknown error"}"
                    _errorMessage.value = errorMsg
                    showToast(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("MealViewModel", "Unexpected error", e)
                val errorMsg = "Something went wrong: ${e.message ?: "Unknown error"}"
                _errorMessage.value = errorMsg
                showToast(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Method using direct API calls (from original MealViewModel)
    fun getRecommendedMeals() {
        viewModelScope.launch {
            _apiIsLoading.value = true
            _apiError.value = null
            try {
                apiMeal.getRecommendedMeals().fold(
                    onSuccess = { response ->
                        _recommendedMeals.value = response.data
                        Log.d("MealViewModel", "Successfully loaded ${response.data.size} meals")
                    },
                    onFailure = { exception ->
                        _apiError.value = exception.message ?: "Unknown error occurred"
                        Log.e("MealViewModel", "Error fetching meals", exception)
                    }
                )
            } catch (e: Exception) {
                _apiError.value = e.message ?: "Unknown error occurred"
                Log.e("MealViewModel", "Exception in getRecommendedMeals", e)
            } finally {
                _apiIsLoading.value = false
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show()
    }

    fun setRawJsonResponse(json: String) {
        _rawJsonResponse.value = json
    }
}