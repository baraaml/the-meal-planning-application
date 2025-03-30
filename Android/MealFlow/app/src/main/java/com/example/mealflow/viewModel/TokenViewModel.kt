package com.example.mealflow.viewModel

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mealflow.network.loginApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
class TokenViewModel : ViewModel() {
    private var _accessToken = MutableLiveData("")
    val accessToken: LiveData<String> get() = _accessToken

    private var _refreshToken = MutableLiveData("")
    val refreshToken: LiveData<String> get() = _refreshToken


    fun updateAccessToken(newAccessToken: String) {
        _accessToken.value = newAccessToken
    }

    fun updateRefreshToken(newRefreshToken: String) {
        _refreshToken.value = newRefreshToken
    }
}
