package com.example.mealflow.ui.screens

import com.example.mealflow.R
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.mealflow.data.model.Ingredient
import com.example.mealflow.data.model.Interactions
import com.example.mealflow.data.model.Meal
import com.example.mealflow.data.model.User
import java.util.Calendar

@Composable
fun HomePage(
    meals: List<Meal>,
    userName: String = "User", // Add userName parameter with default value
    onMealClick: (Meal) -> Unit
) {
    // Get current hour to determine greeting
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

    val greeting = when {
        hourOfDay < 12 -> "Good Morning"
        hourOfDay < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Improved greeting header with user name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$greeting,",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

//             You could add a user avatar or notification icon here
//             IconButton(onClick = { /* Handle profile click */ }) {
//                 Icon(
//                     imageVector = Icons.Default.AccountCircle,
//                     contentDescription = "Profile",
//                     modifier = Modifier.size(40.dp)
//                 )
//             }
        }

        // Section 1: Planned
        SectionHeader("Planned")
        if (meals.isNotEmpty()) {
            MealRowImproved(meals = meals.take(5), onMealClick = onMealClick)
        } else {
            EmptyStateMessage("No planned meals available")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 2: Recommended
        SectionHeader("Recommended")
        if (meals.isNotEmpty()) {
            MealRowImproved(meals = meals.take(5), onMealClick = onMealClick)
        } else {
            EmptyStateMessage("No recommendations available")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 3: Community's Popular
        SectionHeader("Community's Popular")
        if (meals.isNotEmpty()) {
            MealRowImproved(meals = meals.take(5), onMealClick = onMealClick)
        } else {
            EmptyStateMessage("No popular meals available")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 4: You May Like
        SectionHeader("You May Like")
        if (meals.isNotEmpty()) {
            MealRowImproved(meals = meals.take(5), onMealClick = onMealClick)
        } else {
            EmptyStateMessage("No suggestions available")
        }

        // Add bottom space for better scrolling experience
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextButton(onClick = { /* Handle see all click */ }) {
            Text(
                text = "See All",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MealRowImproved(
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
val sampleMeals = listOf(
    createSampleMeal(
        "1",
        "Spaghetti Carbonara",
        "A classic Italian pasta dish with eggs, cheese, and bacon.",
        "https://example.com/spaghetti.jpg",
        listOf("Italian", "Pasta")
    ),
    createSampleMeal(
        "2",
        "Chicken Tikka Masala",
        "Grilled chicken in a spicy curry sauce.",
        "https://example.com/chicken-tikka.jpg",
        listOf("Indian", "Spicy")
    ),
    createSampleMeal(
        "3",
        "Avocado Toast",
        "Simple and healthy breakfast with avocado on toast.",
        "https://example.com/avocado-toast.jpg",
        listOf("Breakfast", "Vegetarian")
    ),
    createSampleMeal(
        "4",
        "Beef Stir Fry",
        "Quick and delicious stir-fried beef with vegetables.",
        "https://example.com/beef-stir-fry.jpg",
        listOf("Asian", "Quick")
    ),
    createSampleMeal(
        "5",
        "Chocolate Cake",
        "Rich and moist chocolate cake for dessert lovers.",
        "https://example.com/chocolate-cake.jpg",
        listOf("Dessert", "Sweet")
    )
)
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
        onClick = onClick  // This is correct but doesn't properly activate the click behavior
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Image takes about 60% of card height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
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
// -------------Preview--------------

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    HomePage(
        meals = sampleMeals,
        userName = "Baraa",
        onMealClick = { /* Preview doesn't need click handler */ }
    )
}

/**
 * Helper function to create sample meals for preview
 */
private fun createSampleMeal(
    id: String,
    name: String,
    description: String,
    imageUrl: String,
    tags: List<String>
): Meal {
    return Meal(
        mealId = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        tags = tags,
        ingredients = listOf(
            Ingredient("Ingredient 1", 1.0, "cup"),
            Ingredient("Ingredient 2", 2.0, "tbsp")
        ),
        instructions = listOf("Step 1: Do something", "Step 2: Do something else"),
        cookware = listOf("Pan", "Pot"),
        preparationTime = 15,
        cookingTime = 30,
        servings = 4,
        caloriesPerServing = 350,
        rating = 4.5,
        reviewsCount = 12,
        createdBy = User("user1", "JohnDoe"),
        isFavorited = false,
        isSaved = false,
        notes = emptyList(),
        interactions = Interactions(100, 50, 2, 10),
        createdAt = "2025-01-01T12:00:00Z",
        updatedAt = "2025-01-01T12:00:00Z"
    )
}