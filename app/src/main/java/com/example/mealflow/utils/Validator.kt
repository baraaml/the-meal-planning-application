package com.example.mealflow.utils


object Validator {
    private val emailRegex = """^[^\s@]+@[^\s@]+\.[^\s@]+$""".toRegex()
    private val usernameRegex = "^[a-zA-Z][a-zA-Z0-9_]{2,29}$".toRegex()
    private val passwordRegex = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*]).{8,64}".toRegex()
    private val otpRegex = "^\\d{6}$".toRegex()

    fun validateUsername(username: String?): String? {
        return when {
            username.isNullOrBlank() -> "Username is required."
            username.length < 3 -> "Username must be at least 3 characters long."
            !usernameRegex.matches(username) -> "Username must start with a letter and can only contain letters, numbers, and underscores."
            else -> null
        }
    }

    fun validateEmail(email: String?): String? {
        return when {
            email.isNullOrBlank() -> "Email is required."
            !emailRegex.matches(email) -> "Please provide a valid email address."
            else -> null
        }
    }

    fun validatePassword(password: String?): String? {
        return when {
            password.isNullOrBlank() -> "Password is required."
            password.length < 8 -> "Password must be at least 8 characters long."
            password.length > 64 -> "Password cannot be longer than 64 characters."
            !passwordRegex.matches(password) -> "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (!@#\$%^&*)."
            else -> null
        }
    }

    fun validateOtp(otp: String?): String? {
        return when {
            otp.isNullOrBlank() -> "OTP is required."
            !otpRegex.matches(otp) -> "Invalid OTP."
            else -> null
        }
    }
}