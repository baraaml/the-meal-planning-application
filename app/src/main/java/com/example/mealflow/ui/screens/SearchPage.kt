package com.example.mealflow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mealflow.data.model.Meal
import com.example.mealflow.ui.components.MealItem
import com.example.mealflow.viewModel.MealViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchPage(
    viewModel: MealViewModel,
    onMealClick: (Any) -> Unit,
    navController: NavHostController,
    meals: List<Meal>
) {
    val meals by viewModel.meals.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Focus management
    val focusManager = LocalFocusManager.current
    var isSearchFocused by remember { mutableStateOf(false) }

    // Remember search history
    val context = LocalContext.current
    val searchHistory = remember { mutableStateListOf<String>() }
    var showHistory by remember { mutableStateOf(false) }

    // Load search history from ViewModel if available
    LaunchedEffect(Unit) {
        // Ideally, this would come from ViewModel
        // For now, we'll use a dummy history for demonstration
        if (searchHistory.isEmpty()) {
            // This is temporary - should come from persistent storage via ViewModel
            searchHistory.addAll(listOf("Pasta", "Chicken", "Salad"))
        }
    }

    // Filter options
    val filterOptions = listOf("All", "Breakfast", "Lunch", "Dinner", "Dessert", "Vegetarian", "Vegan", "Gluten-Free")
    var selectedFilter by remember { mutableStateOf("All") }

    // Filter meals based on search query and selected filter
    val filteredMeals = remember(searchQuery, meals, selectedFilter) {
        meals.filter { meal ->
            val matchesSearch = searchQuery.isEmpty() ||
                    meal.name.contains(searchQuery, ignoreCase = true) ||
                    meal.description.contains(searchQuery, ignoreCase = true) ||
                    meal.tags.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesFilter = selectedFilter == "All" ||
                    meal.tags.any { it.equals(selectedFilter, ignoreCase = true) }

            matchesSearch && matchesFilter
        }
    }

    // Submit search function
    fun submitSearch() {
        if (searchQuery.isNotEmpty()) {
            // Add to history if not already there
            if (!searchHistory.contains(searchQuery)) {
                searchHistory.add(0, searchQuery) // Add to beginning
                // Keep only the most recent 5 searches
                if (searchHistory.size > 5) {
                    searchHistory.removeAt(searchHistory.size - 1)
                }
            } else {
                // Move to top if already exists
                searchHistory.remove(searchQuery)
                searchHistory.add(0, searchQuery)
            }

            // In a real app, you'd save this to persistent storage via ViewModel
            // viewModel.saveSearchHistory(searchHistory)

            // Hide keyboard and history after submission
            focusManager.clearFocus()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar with icons
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                // Only show history if field is focused and not empty
                showHistory = isSearchFocused && it.isNotEmpty() && searchHistory.isNotEmpty()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .onFocusChanged { focusState ->
                    isSearchFocused = focusState.isFocused
                    showHistory = focusState.isFocused && searchQuery.isNotEmpty() && searchHistory.isNotEmpty()
                }
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter) {
                        submitSearch()
                        true
                    } else {
                        false
                    }
                },
            placeholder = { Text("Search for meals...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    submitSearch()
                }
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        showHistory = isSearchFocused && searchHistory.isNotEmpty()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search"
                        )
                    }
                }
            }
        )

        // Filter options
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = {
                        selectedFilter = filter
                        // Apply filter immediately
                        if (searchQuery.isNotEmpty()) {
                            submitSearch()
                        }
                    },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // Search history dropdown
        if (showHistory && searchHistory.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Searches", style = MaterialTheme.typography.titleSmall)
                        IconButton(onClick = {
                            searchHistory.clear()
                            showHistory = false
                            // In a real app, clear from persistent storage too:
                            // viewModel.clearSearchHistory()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear History",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Divider()

                    // Show last 5 searches
                    searchHistory.take(5).forEach { search ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchQuery = search
                                    showHistory = false
                                    submitSearch() // Apply the search immediately when clicked
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(search)
                            }
                            IconButton(
                                onClick = {
                                    searchHistory.remove(search)
                                    // If history becomes empty, hide dropdown
                                    if (searchHistory.isEmpty()) {
                                        showHistory = false
                                    }
                                    // In a real app, remove from persistent storage too:
                                    // viewModel.removeSearchHistoryItem(search)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Show loading indicator, error message, or meal grid
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            filteredMeals.isEmpty() && errorMessage == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No meals found",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        if (searchQuery.isNotEmpty() || selectedFilter != "All") {
                            Text(
                                text = "Try changing your search or filters",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "Something went wrong", color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 columns grid
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredMeals.size) { index ->
                        val meal = filteredMeals[index]
                        MealItem(
                            meal = meal,
                            onClick = {
                                // Save search when user clicks a meal
                                submitSearch()
                                onMealClick(meal)
                            }
                        )
                    }
                }
            }
        }
    }
}

