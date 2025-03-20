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
fun PlannerPage() {
    Box(modifier = Modifier.fillMaxSize()){
        Text(text = "Planner", textAlign = Center)
    }
}