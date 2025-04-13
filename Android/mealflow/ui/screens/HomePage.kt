package com.example.mealflow.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mealflow.data.model.Meal
import com.example.mealflow.viewModel.MealViewModel
import java.util.Calendar
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.mealflow.ui.components.EmptyStateMessage
import com.example.mealflow.ui.components.MealSection
import com.example.mealflow.ui.components.SectionHeader

@Composable
fun HomePage(
    meals: List<Meal>,
    userName: String = "User",
    onMealClick: (Meal) -> Unit,
    viewModel: MealViewModel? = null
) {
    // Get current hour to determine greeting
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

    val greeting = when {
        hourOfDay < 12 -> "Good Morning"
        hourOfDay < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    // Observe view model states from different meal types
    val isLoading by viewModel?.isLoading?.collectAsState(false) ?: remember { mutableStateOf(false) }
    val errorMessage by viewModel?.errorMessage?.collectAsState(null) ?: remember { mutableStateOf<String?>(null) }

    // Get the specific meal lists
    val recommendedMeals by viewModel?.recommendedMeals?.collectAsState(emptyList())
        ?: remember { mutableStateOf(emptyList()) }
    val trendingMeals by viewModel?.trendingMeals?.collectAsState(emptyList())
        ?: remember { mutableStateOf(emptyList()) }
    val collaborativeMeals by viewModel?.collaborativeMeals?.collectAsState(emptyList())
        ?: remember { mutableStateOf(emptyList()) }

    // State to track whether an error was already shown
    var errorShown by remember { mutableStateOf(false) }

    // Lifecycle observer to refresh data when screen becomes active
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refresh data when screen becomes active
                viewModel?.fetchAllMealTypes()
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
                // Section 1: Planned - This section might be for user's planned meals
                // For now use recommended as placeholder until we have planned meals API
                SectionHeader("Planned")
                if (recommendedMeals.isNotEmpty()) {
                    MealSection(meals = recommendedMeals.take(minOf(5, recommendedMeals.size)), onMealClick = onMealClick)
                } else {
                    EmptyStateMessage("No planned meals available")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 2: Recommended - Shows content-based recommendations
                SectionHeader("Recommended")
                if (recommendedMeals.isNotEmpty()) {
                    MealSection(meals = recommendedMeals.take(minOf(5, recommendedMeals.size)), onMealClick = onMealClick)
                } else {
                    EmptyStateMessage("No recommendations available")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 3: Community's Popular - Shows trending meals
                SectionHeader("Community's Popular")
                if (trendingMeals.isNotEmpty()) {
                    MealSection(meals = trendingMeals.take(minOf(5, trendingMeals.size)), onMealClick = onMealClick)
                } else {
                    EmptyStateMessage("No popular meals available")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 4: You May Like - Shows collaborative filtering recommendations
                SectionHeader("You May Like")
                if (collaborativeMeals.isNotEmpty()) {
                    MealSection(meals = collaborativeMeals.take(minOf(5, collaborativeMeals.size)), onMealClick = onMealClick)
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


