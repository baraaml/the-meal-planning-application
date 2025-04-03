package com.example.mealflow.viewModel

import androidx.lifecycle.ViewModel
import com.example.mealflow.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    // Example: Each list item is just an Int for the drawable resource
    private val _plannedItems = MutableStateFlow(
        listOf(R.drawable.ok1, R.drawable.ok2, R.drawable.ok3, R.drawable.ok4)
    )
    val plannedItems: StateFlow<List<Int>> = _plannedItems

    private val _youMayLikeItems = MutableStateFlow(
        listOf(R.drawable.ok1, R.drawable.ok2, R.drawable.ok3, R.drawable.ok4)
    )
    val youMayLikeItems: StateFlow<List<Int>> = _youMayLikeItems

    private val _popularItems = MutableStateFlow(
        listOf(R.drawable.ok1, R.drawable.ok2, R.drawable.ok3, R.drawable.ok4)
    )
    val popularItems: StateFlow<List<Int>> = _popularItems
}