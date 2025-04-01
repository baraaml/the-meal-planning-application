package com.example.mealflow.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealflow.database.community.CommunityRepository
import com.example.mealflow.database.community.GetCommunityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GetCommunityViewModel(private val repository: CommunityRepository) : ViewModel() {
    private val _communities = MutableStateFlow<List<GetCommunityEntity>>(emptyList())
    val communities: StateFlow<List<GetCommunityEntity>> = _communities.asStateFlow()

    init {
        Log.d("GetCommunityViewModel", "ViewModel initialized, calling fetchCommunities()")
        fetchCommunities()
    }

    private fun fetchCommunities() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("GetCommunityViewModel", "Fetching communities from DB...")

            val data = repository.getCommunitiesFromFlow() // Use a new function in the repo
            Log.d("GetCommunityViewModel", "Communities from DB: $data")

            _communities.value = data
        }
    }
    fun fetchAndStoreCommunities() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("GetCommunityViewModel", "Fetching and storing communities...")

            repository.fetchCommunities() // Fetch new data
            val updatedData = repository.getCommunitiesFromFlow() // Recover data after update

            Log.d("GetCommunityViewModel", "Updated Communities: $updatedData")

            _communities.value = updatedData // Update the StateFlow with new data
        }
    }

//    fun fetchAndStoreCommunities() {
//        viewModelScope.launch {
//            Log.d("GetCommunityViewModel", "Calling repository.fetchCommunities()")
//            repository.fetchCommunities()
//        }
//    }
    suspend fun getCommunitiesFromDB() = repository.getCommunitiesFromDB()
}
