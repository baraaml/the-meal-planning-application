package com.example.mealflow.database.token

import android.content.Context

class TokenManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .apply()
    }

    fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)

    fun getRefreshToken(): String? = sharedPreferences.getString("refresh_token", null)

    fun clearTokens() {
        sharedPreferences.edit().clear().apply()
    }
    fun clearAccessToken() {
        sharedPreferences.edit().remove("access_token").apply()
    }

    fun clearRefreshToken() {
        sharedPreferences.edit().remove("refresh_token").apply()
    }
}
