package com.example.mealflow.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mealflow.network.loginApi
import kotlinx.coroutines.launch

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


    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = _passwordVisible.value?.not()
    }

//    fun loginButton(email: String, password: String, navController: NavController,viewModel: LoginViewModel) {
//        loginApi(email, password, navController, viewModel)
//    }
fun loginButton(email: String, password: String, navController: NavController) {
    viewModelScope.launch {
        loginApi(email, password, navController, this@LoginViewModel)
    }
}


    private val _loginMessage = MutableLiveData<String>()
    val loginMessage: LiveData<String> get() = _loginMessage

    fun setLoginMessage(message: String) {
        _loginMessage.value = message
    }
}
