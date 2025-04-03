package com.example.mealflow.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class SelectedViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    fun setSelectedCommunity(id: String) {
        savedStateHandle["communityId"] = id
    }

    fun getSelectedCommunity(): String? {
        return savedStateHandle["communityId"]
    }
}
