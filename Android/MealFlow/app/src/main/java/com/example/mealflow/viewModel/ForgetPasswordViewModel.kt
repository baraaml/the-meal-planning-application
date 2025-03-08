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

    //private var _showOtpPopup = MutableLiveData(false)
    //val context = LocalContext.current  // ✅ الحصول على الـ Context

    private val _ForgetPasswordMessage = MutableLiveData<String?>()
    val registrationMessage: LiveData<String?> get() = _ForgetPasswordMessage

    private val _navigateToOtp = MutableLiveData<Boolean>()
    val navigateToOtp: LiveData<Boolean> get() = _navigateToOtp

    // 🔹 إضافة متغير `token`
    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    // 🔹 دالة لتحديث `token`
    fun updateToken(newToken: String) {
        _token.value = newToken
    }
//    private val _showErrorPopup = MutableLiveData<Boolean>()
//    val showErrorPopup: LiveData<Boolean> get() = _showErrorPopup

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updateRepassword(newRepassword: String) {
        _repassword.value = newRepassword
    }

    fun toggleShowErrorPopupVisibility() {
        _showErrorPopup.value = _showErrorPopup.value?.not()
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = _passwordVisible.value?.not()
    }

    private val _showErrorPopup = MutableLiveData(false)
    val showErrorPopup: LiveData<Boolean> get() = _showErrorPopup

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun validateInputs(username: String, email: String, password: String): Boolean {
        val errors = mutableListOf<String>()

        val usernameError = Validator.validateUsername(username)
        val emailError = Validator.validateEmail(email)
        val passwordError = Validator.validatePassword(password)

        usernameError?.let { errors.add(it) }
        emailError?.let { errors.add(it) }
        passwordError?.let { errors.add(it) }

        if (errors.isNotEmpty()) {
            _errorMessage.value = errors.joinToString("\n")
            _showErrorPopup.value = true
            return false // ❌ هناك أخطاء، لا تكمل التسجيل
        }

        return true // ✅ لا توجد أخطاء، يمكن المتابعة
    }

    fun dismissErrorPopup() {
        _showErrorPopup.value = false
    }
}
