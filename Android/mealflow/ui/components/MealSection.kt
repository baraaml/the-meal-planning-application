package com.example.mealflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mealflow.R
import com.example.mealflow.data.model.Meal
import com.example.mealflow.ui.screens.AllMealsPage

@Composable
fun MealSection(
    meals: List<Meal>,
    onMealClick: (Meal) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(meals) { meal ->
            HomeMealCard(
                meal = meal,
                onClick = {
                    try {
                        onMealClick(meal)
                    } catch (e: Exception) {
                        // Log error but don't crash
                        android.util.Log.e(
                            "MealRowImproved",
                            "Error handling meal click for meal ID: ${meal.mealId}",
                            e
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun HomeMealCard(
    meal: Meal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(220.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Image takes about 60% of card height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                // Properly load the meal image with AsyncImage
                AsyncImage(
                    model = meal.imageUrl,
                    contentDescription = meal.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(id = R.drawable.android_robot),
                    error = painterResource(id = R.drawable.android_robot)
                )
            }

            // Content section takes remaining space
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Meal name
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Brief description or category
                Text(
                    text = meal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rating or cooking time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display the first tag or default text
                    Text(
                        text = meal.tags.firstOrNull() ?: "Meal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun MealGridItem(
    meal: Meal,
    onMealClick: (Meal) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onMealClick(meal) }
    ) {
        // Meal Image, Title, and other details in each grid cell
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Placeholder for meal image (use actual image URL in a real app)
            AsyncImage(
                model = meal.imageUrl,
                contentDescription = meal.name,
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = meal.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = meal.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
