package com.example.mealflow.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mealflow.utils.Validator

class ForgetPasswordViewModel : ViewModel() {
    private var _email = MutableLiveData("")
    val email: LiveData<String> get() = _email

    private var _password = MutableLiveData("")
    val password: LiveData<String> get() = _password

    private var _repassword = MutableLiveData("")
    val repassword: LiveData<String> get() = _repassword

    private var _passwordVisible = MutableLiveData(false)
    val passwordVisible: LiveData<Boolean> get() = _passwordVisible

    // 🔹 Add a variable `token`
    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    // 🔹 function to update `token`
    fun updateToken(newToken: String) {
        _token.value = newToken
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    // Add loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updaterepassword(newrepassword: String) {
        _repassword.value = newrepassword
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = _passwordVisible.value?.not()
    }


    fun validateInputs(username: String, email: String, password: String): Boolean {
        val errors = mutableListOf<String>()

        val usernameError = Validator.validateUsername(username)
        val emailError = Validator.validateEmail(email)
        val passwordError = Validator.validatePassword(password)

        usernameError?.let { errors.add(it) }
        emailError?.let { errors.add(it) }
        passwordError?.let { errors.add(it) }

        return true // ✅ No errors, can continue
    }

    fun setLoading(loading: Boolean) {
        Log.d("LOADING_STATE", "🔄 isLoading = $loading") // ✅ تتبع التحديثات
        _isLoading.value = loading
    }
    private val _navigateToTestPage = MutableLiveData<Boolean>(false)
    val navigateToTestPage: LiveData<Boolean> = _navigateToTestPage

    fun setNavigateToTestPage(navigate: Boolean) {
        _navigateToTestPage.value = navigate
    }
}
