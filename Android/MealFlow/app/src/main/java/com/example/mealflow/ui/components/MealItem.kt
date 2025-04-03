package com.example.mealflow.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mealflow.data.model.Meal
import com.example.mealflow.R
import androidx.compose.ui.res.painterResource
@Composable
fun MealItem(meal: Meal, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Display meal image with safe handling
            AsyncImage(
                model = meal.imageUrl.takeIf { meal.imageUrl.isNotBlank() } ?: R.drawable.neptune_placeholder_48,
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.neptune_placeholder_48),
                error = painterResource(id = R.drawable.neptune_placeholder_48)
            )

            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                // Safely display meal name
                Text(
                    text = meal.name.takeIf { meal.name.isNotBlank() } ?: "Unnamed Meal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Safely display meal description
                Text(
                    text = meal.description.takeIf { meal.description.isNotBlank() } ?: "No description available",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Display tags and rating if available
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    meal.tags.firstOrNull()?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "Rating: ${meal.rating}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
