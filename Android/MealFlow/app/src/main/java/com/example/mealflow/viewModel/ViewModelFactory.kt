package com.example.mealflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mealflow.database.community.CommunityRepository

class ViewModelFactory(private val repository: CommunityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GetCommunityViewModel::class.java) -> GetCommunityViewModel(repository) as T
//            modelClass.isAssignableFrom(CommunityViewModel::class.java) -> CommunityViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

