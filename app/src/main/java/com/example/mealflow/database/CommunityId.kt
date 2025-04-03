package com.example.mealflow.database

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val COMMUNITY_ID_KEY = stringPreferencesKey("community_id")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    // حفظ البيانات
    suspend fun saveUsername(username: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
        }
    }

    suspend fun saveCommunityId(communityId: String) {
        context.dataStore.edit { prefs ->
            prefs[COMMUNITY_ID_KEY] = communityId
        }
    }

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = isDarkMode
        }
    }

    // استرجاع البيانات باستخدام Flow
    fun getUsername() = context.dataStore.data.map { prefs ->
        prefs[USERNAME_KEY] ?: "Unknown"
    }

    fun getCommunityId() = context.dataStore.data.map { prefs ->
        prefs[COMMUNITY_ID_KEY] ?: "N/A"
    }

    fun isDarkModeEnabled() = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY] ?: false
    }

    // استرجاع القيم بشكل متزامن
    suspend fun getUsernameSync(): String {
        return context.dataStore.data.first()[USERNAME_KEY] ?: "Unknown"
    }

    suspend fun getCommunityIdSync(): String {
        return context.dataStore.data.first()[COMMUNITY_ID_KEY] ?: "N/A"
    }

    suspend fun isDarkModeEnabledSync(): Boolean {
        return context.dataStore.data.first()[DARK_MODE_KEY] ?: false
    }

    // حذف جميع البيانات
    suspend fun clearPreferences() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
