package com.example.mealflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import com.example.mealflow.data.model.Meal
import com.example.mealflow.ui.components.MealGridItem

@Composable
fun AllMealsPage(
    title: String,
    meals: List<Meal>,
    onMealClick: (Meal) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show progress if meals list is empty
        if (meals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // LazyVerticalGrid for grid layout
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp), // Adjust item size
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(meals) { meal ->
                    MealGridItem(meal = meal, onMealClick = onMealClick)
                }
            }
        }
    }
}