package com.example.mealflow.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.navigation.NavHostController
import com.example.mealflow.viewModel.MealViewModel

@Composable
fun GroceriesPage() {
    Box(modifier = Modifier.fillMaxSize()){
        Text(text = "Groceries", textAlign = Center)
    }
}