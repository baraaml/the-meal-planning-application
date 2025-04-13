package com.example.mealflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mealflow.data.model.Meal
import com.example.mealflow.R
import com.example.mealflow.data.model.Ingredient
import com.example.mealflow.data.model.Interactions
import com.example.mealflow.data.model.Note
import com.example.mealflow.data.model.User

@Composable
fun MealCard(meal: Meal, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = meal.imageUrl.takeIf { meal.imageUrl.isNotBlank() } ?: R.drawable.neptune_placeholder_48,
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.neptune_placeholder_48),
                error = painterResource(id = R.drawable.neptune_placeholder_48)
            )

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
                Text(
                    text = meal.name.takeIf { it.isNotBlank() } ?: "Unnamed Meal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = meal.description.takeIf { it.isNotBlank() } ?: "No description available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    meal.tags.firstOrNull()?.let { tag ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${meal.rating}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MealCardPreview() {
    MealCard(
        meal = Meal(
            mealId = "1",
            name = "Spaghetti Carbonara",
            description = "Rich and creamy pasta with crispy pancetta.",
            imageUrl = "https://via.placeholder.com/150",
            tags = listOf("Italian", "Pasta"),
            ingredients = listOf(
                Ingredient(name = "Spaghetti", quantity = 200.0, unit = "grams"),
                Ingredient(name = "Pancetta", quantity = 100.0, unit = "grams"),
                Ingredient(name = "Eggs", quantity = 3.0, unit = "pieces"),
                Ingredient(name = "Parmesan Cheese", quantity = 50.0, unit = "grams"),
                Ingredient(name = "Black Pepper", quantity = 1.0, unit = "tsp"),
            ),
            instructions = listOf(
                "Boil the spaghetti in salted water.",
                "Fry the pancetta until crispy.",
                "Whisk eggs and Parmesan together.",
                "Combine spaghetti, pancetta, and egg mixture.",
                "Season with black pepper and serve hot."
            ),
            cookware = listOf("Pot", "Pan", "Whisk"),
            preparationTime = 15,
            cookingTime = 20,
            servings = 2,
            caloriesPerServing = 550,
            rating = 4.7,
            reviewsCount = 128,
            createdBy = User(
                userId = "user_1",
                username = "chef_john",
                profilePicture = "https://via.placeholder.com/50"
            ),
            isFavorited = true,
            isSaved = false,
            notes = listOf(
                Note(
                    noteId = "note_1",
                    user = User(
                        userId = "user_2",
                        username = "foodie123",
                        profilePicture = "https://via.placeholder.com/50"
                    ),
                    comment = "Loved this recipe! Turned out perfect.",
                    imageUrl = "https://via.placeholder.com/100",
                    tags = listOf("Easy", "Quick"),
                    didCook = true,
                    likes = 12,
                    dislikes = 0
                )
            ),
            interactions = Interactions(
                views = 340,
                likes = 85,
                dislikes = 3,
                shares = 10
            ),
            createdAt = "2025-04-13T12:00:00Z",
            updatedAt = "2025-04-13T12:00:00Z"
        ),
        onClick = {}
    )
}
