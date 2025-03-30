package com.example.mealflow.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//class CommunityViewModel : ViewModel() {
//    private var _communityName = MutableLiveData("")
//    val communityName: LiveData<String> get() = _communityName
//
//    fun updateCommunityName(newEmail: String) {
//        _communityName.value = newEmail
//    }
//
//    private var _communityDescription = MutableLiveData("")
//    val communityDescription: LiveData<String> get() = _communityDescription
//
//    fun updateCommunityDescription(newEmail: String) {
//        _communityDescription.value = newEmail
//    }
//
//    private var _recipeCreationPermission = MutableLiveData("")
//    val recipeCreationPermission: LiveData<String> get() = _recipeCreationPermission
//
//    private var _categories = MutableLiveData("")
//    val categories: LiveData<String> get() = _categories
//
//
//    // 🔹 Add the `token` variable
//    private val _token = MutableLiveData<String?>()
//    val token: LiveData<String?> get() = _token
//
//    // 🔹 Function to update `token`
//    fun updateToken(newToken: String) {
//        _token.value = newToken
//    }
//
//    var selectedImageUri by mutableStateOf<Uri?>(null)
//        private set
//
//    fun setImageUri(uri: Uri?) {
//        selectedImageUri = uri
//    }
//}
//

class CommunityViewModel : ViewModel() {
    private var _communityName = MutableLiveData("")
    val communityName: LiveData<String> get() = _communityName

    fun updateCommunityName(newName: String) {
        _communityName.value = newName
    }

    private var _communityDescription = MutableLiveData("")
    val communityDescription: LiveData<String> get() = _communityDescription

    fun updateCommunityDescription(newDescription: String) {
        _communityDescription.value = newDescription
    }

    private var _recipeCreationPermission = MutableLiveData("")
    val recipeCreationPermission: LiveData<String> get() = _recipeCreationPermission

    fun updateRecipeCreationPermission(permission: String) {
        _recipeCreationPermission.value = permission
    }

    private var _categories = MutableLiveData<List<String>>(emptyList())
    val categories: LiveData<List<String>> get() = _categories

    fun updateCategories(newCategories: List<String>) {
        _categories.value = newCategories
    }

    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun setImageUri(uri: Uri?) {
        selectedImageUri = uri
    }
}
