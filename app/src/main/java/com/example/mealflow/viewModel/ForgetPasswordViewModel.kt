package com.example.mealflow.viewModel

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

    private val _forgetPasswordMessage = MutableLiveData<String?>()
    val forgetPasswordMessage: LiveData<String?> get() = _forgetPasswordMessage

    private val _navigateToOtp = MutableLiveData<Boolean>()
    val navigateToOtp: LiveData<Boolean> get() = _navigateToOtp

    // Token handling
    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    private val _showErrorPopup = MutableLiveData(false)
    val showErrorPopup: LiveData<Boolean> get() = _showErrorPopup

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun updateToken(newToken: String) {
        _token.value = newToken
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updateRepassword(newRepassword: String) {
        _repassword.value = newRepassword
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = _passwordVisible.value?.not()
    }

    fun toggleShowErrorPopupVisibility() {
        _showErrorPopup.value = _showErrorPopup.value?.not()
    }

    fun validateInputs(email: String, password: String, repassword: String): Boolean {
        val errors = mutableListOf<String>()

        val emailError = Validator.validateEmail(email)
        val passwordError = Validator.validatePassword(password)

        // Check if passwords match
        if (password != repassword) {
            errors.add("Passwords do not match")
        }

        emailError?.let { errors.add(it) }
        passwordError?.let { errors.add(it) }

        if (errors.isNotEmpty()) {
            _errorMessage.value = errors.joinToString("\n")
            _showErrorPopup.value = true
            return false
        }

        return true
    }

    fun dismissErrorPopup() {
        _showErrorPopup.value = false
    }
}