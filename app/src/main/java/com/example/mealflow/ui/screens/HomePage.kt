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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mealflow.data.model.Meal
import com.example.mealflow.viewModel.MealViewModel
import java.util.Calendar
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.mealflow.data.model.Ingredient
import com.example.mealflow.data.model.Interactions
import com.example.mealflow.data.model.Note
import com.example.mealflow.data.model.User

@Composable
fun HomePage(
    meals: List<Meal>,
    userName: String = "User",
    onMealClick: (Meal) -> Unit,
    viewModel: MealViewModel? = null // Added viewModel parameter
) {
    // Get current hour to determine greeting
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

    val greeting = when {
        hourOfDay < 12 -> "Good Morning"
        hourOfDay < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    // Observe view model states if available
    val isLoading by viewModel?.isLoading?.collectAsState(false) ?: remember { mutableStateOf(false) }
    val errorMessage by viewModel?.errorMessage?.collectAsState(null) ?: remember { mutableStateOf<String?>(null) }

    // State to track whether an error was already shown
    var errorShown by remember { mutableStateOf(false) }

    // Lifecycle observer to refresh data when screen becomes active
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refresh data when screen becomes active
                viewModel?.fetchRecommendedMeals()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

                // Add refresh button
                if (viewModel != null) {
                    IconButton(onClick = { viewModel.refreshMeals() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Loading indicator for initial load
            if (isLoading && meals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Section 1: Planned
                SectionHeader("Planned")
                if (meals.isNotEmpty()) {
                    MealRowImproved(meals = meals.take(minOf(5, meals.size)), onMealClick = onMealClick)
                } else {
                    EmptyStateMessage("No planned meals available")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 2: Recommended
                SectionHeader("Recommended")
                if (meals.isNotEmpty()) {
                    MealRowImproved(meals = meals.take(minOf(5, meals.size)), onMealClick = onMealClick)
                } else {
                    EmptyStateMessage("No recommendations available")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 3: Community's Popular
                SectionHeader("Community's Popular")
                if (meals.isNotEmpty()) {
                    MealRowImproved(meals = meals.take(minOf(5, meals.size)), onMealClick = onMealClick)
                } else {
                    EmptyStateMessage("No popular meals available")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 4: You May Like
                SectionHeader("You May Like")
                if (meals.isNotEmpty()) {
                    MealRowImproved(meals = meals.take(minOf(5, meals.size)), onMealClick = onMealClick)
                } else {
                    EmptyStateMessage("No suggestions available")
                }
            }

            // Add bottom space for better scrolling experience
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Show error message as a snackbar at the bottom
        if (!errorMessage.isNullOrBlank() && !errorShown) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { errorShown = true }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(errorMessage ?: "Unknown error occurred")
            }

            // Mark error as shown after a delay
            LaunchedEffect(errorMessage) {
                kotlinx.coroutines.delay(5000)
                errorShown = true
            }
        }

        // Overlay loading indicator for refresh operations
        if (isLoading && meals.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    val sampleMeals = listOf(
        Meal(
            mealId = "1",
            name = "Spaghetti",
            description = "Delicious spaghetti with tomato sauce",
            imageUrl = "https://via.placeholder.com/150",
            tags = listOf("Italian", "Pasta"),
            ingredients = listOf(
                Ingredient(name = "Spaghetti", quantity = 200.0, unit = "grams"),
                Ingredient(name = "Tomato Sauce", quantity = 100.0, unit = "ml")
            ),
            instructions = listOf("Boil water", "Add pasta", "Cook sauce", "Mix together"),
            cookware = listOf("Pot", "Pan"),
            preparationTime = 10,
            cookingTime = 15,
            servings = 2,
            caloriesPerServing = 350,
            rating = 4.5,
            reviewsCount = 100,
            createdBy = User(userId = "user_123", username = "ChefMaster"),
            isFavorited = true,
            isSaved = false,
            notes = listOf(
                Note(
                    noteId = "note_1",
                    user = User(userId = "user_456", username = "Foodie123"),
                    comment = "Loved it!",
                    didCook = true,
                    likes = 10,
                    dislikes = 1
                )
            ),
            interactions = Interactions(views = 500, likes = 50, dislikes = 2, shares = 5),
            createdAt = "2023-01-01",
            updatedAt = "2023-01-02"
        )
    )

    HomePage(
        meals = sampleMeals,
        userName = "Abdelrahman",
        onMealClick = { /* Do nothing for preview */ }
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}