package com.example.mealflow.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mealflow.utils.Validator

class RegisterViewModel : ViewModel(){
    private var _username = MutableLiveData("")
    val username: LiveData<String> get() = _username

    private var _email = MutableLiveData("")
    val email: LiveData<String> get() = _email

    private var _password = MutableLiveData("")
    val password: LiveData<String> get() = _password

    private var _repassword = MutableLiveData("")
    val repassword: LiveData<String> get() = _repassword

    private var _passwordVisible = MutableLiveData(false)
    val passwordVisible: LiveData<Boolean> get() = _passwordVisible

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = _passwordVisible.value?.not()
    }
    fun setEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun getEmail(): String {
        return _email.value ?: ""
    }


    fun validateInputs(username: String, email: String, password: String): Boolean {
        val errors = mutableListOf<String>()

        val usernameError = Validator.validateUsername(username)
        val emailError = Validator.validateEmail(email)
        val passwordError = Validator.validatePassword(password)

        usernameError?.let { errors.add(it) }
        emailError?.let { errors.add(it) }
        passwordError?.let { errors.add(it) }

        return true // ✅ No errors, you can continue
    }
}