package com.example.mealflow.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mealflow.network.loginApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class LoginViewModel : ViewModel() {
    private var _email = MutableLiveData("")
    val email: LiveData<String> get() = _email

    private var _password = MutableLiveData("")
    val password: LiveData<String> get() = _password

    private var _passwordVisible = MutableLiveData(false)
    val passwordVisible: LiveData<Boolean> get() = _passwordVisible

    private val _registrationMessage = MutableLiveData<String?>()
    val registrationMessage: LiveData<String?> get() = _registrationMessage

    private val _navigateToOtp = MutableLiveData<Boolean>()
    val navigateToOtp: LiveData<Boolean> get() = _navigateToOtp

    // Add a new LiveData to track navigation to home screen
    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> get() = _navigateToHome

    // Add loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // This will be set to true when login is successful to trigger data fetching
    private val _loginSuccessful = MutableLiveData<Boolean>()
    val loginSuccessful: LiveData<Boolean> get() = _loginSuccessful

    private val _loginMessage = MutableLiveData<String>()
    val loginMessage: LiveData<String> get() = _loginMessage

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = _passwordVisible.value?.not()
    }

    fun loginButton(email: String, password: String, navController: NavController) {
        // Set loading state to true before API call
        _isLoading.value = true

        viewModelScope.launch {
            try {
                loginApi(email, password, navController, this@LoginViewModel)
            } catch (e: Exception) {
                // If there's an exception, set error message and reset loading state
                _loginMessage.value = "Login failed: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun setLoginMessage(message: String) {
        _loginMessage.value = message
    }

    // Add a function to navigate to home screen
    fun navigateToHomeScreen() {
        // First mark login as successful to trigger data fetching
        _loginSuccessful.postValue(true)  // Use postValue for background thread safety

        // Add a small delay to ensure data fetching starts before navigation
        viewModelScope.launch {
            delay(300) // Small delay to let data fetching start
            _navigateToHome.postValue(true)
            _isLoading.postValue(false)
        }
    }

    // Add a function to reset navigation flag after navigation completes
    fun onHomeNavigationComplete() {
        _navigateToHome.value = false
    }

    // Reset the login successful flag (useful for logout)
    fun resetLoginState() {
        _loginSuccessful.value = false
    }

    // Set loading state
    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}