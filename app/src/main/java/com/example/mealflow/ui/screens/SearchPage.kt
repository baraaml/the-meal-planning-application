package com.example.mealflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mealflow.data.model.Meal
import com.example.mealflow.ui.components.MealItem
import com.example.mealflow.viewModel.MealViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchPage(
    meals: List<Meal>,
    onMealClick: (Meal) -> Unit,
    viewModel: MealViewModel? = null,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // Collect states from viewModel if available
    val isLoading by viewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }
    val errorMessage by viewModel?.errorMessage?.collectAsState() ?: remember { mutableStateOf<String?>(null) }

    // Local state for search active tracking
    var isSearchActive by remember { mutableStateOf(false) }

    // Execute search with proper debounce
    fun executeSearch(query: String) {
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(500) // Wait for user to finish typing
            isSearchActive = query.isNotEmpty()
            viewModel?.searchMeals(query)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search header and bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search bar - takes most of the space
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newQuery ->
                    searchQuery = newQuery
                    executeSearch(newQuery)
                },
                modifier = Modifier
                    .weight(1f),
                label = { Text("Search meals") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            executeSearch("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true
            )

            // Refresh button
            if (viewModel != null) {
                IconButton(
                    onClick = { viewModel.refreshMeals() },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        }

        // Content area
        Box(modifier = Modifier.fillMaxSize()) {
            // Loading indicator for full page loading
            if (isLoading && meals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading meals...")
                    }
                }
            }
            // No results message
            else if (meals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = if (isSearchActive) {
                                "No meals found matching \"$searchQuery\""
                            } else {
                                "No meals available. Try refreshing!"
                            },
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (viewModel != null) {
                            Button(
                                onClick = { viewModel.refreshMeals() }
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Refresh")
                            }
                        }
                    }
                }
            }
            // Meals grid - when we have data
            else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(meals) { meal ->
                        MealItem(
                            meal = meal,
                            onClick = { onMealClick(meal) }
                        )
                    }
                }

                // Show a small loading indicator for search refreshes
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            // Error snackbar if there's an error message
            if (!errorMessage.isNullOrBlank()) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel?.refreshMeals() }) {
                            Text("Retry")
                        }
                    }
                ) {
                    Text(errorMessage ?: "Unknown error occurred")
                }
            }
        }
    }
}