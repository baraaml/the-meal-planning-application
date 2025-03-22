package com.example.mealflow.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mealflow.R
import com.example.mealflow.data.model.Meal
import com.example.mealflow.data.samples.sampleMeal
import com.example.mealflow.ui.components.NoteItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(meal: Meal, onNavigateBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = meal.name,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.sfpro))
                    )
                        },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Favorite button
                    IconButton(onClick = { /* Toggle favorite */ }) {
                        Icon(
                            if (meal.isFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (meal.isFavorited) "Remove from favorites" else "Add to favorites"
                        )
                    }
                    // Save button
                    IconButton(onClick = { /* Toggle save */ }) {
                        Icon(
                            if (meal.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = if (meal.isSaved) "Remove from saved" else "Save recipe"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Header section
            AsyncImage(
                model = meal.imageUrl,
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.android_robot), // Placeholder image
                error = painterResource(id = R.drawable.android_robot) // Error image
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = meal.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.sf_pro_rounded_heavy))

            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                meal.tags.forEach { tag ->
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(
                                text = tag,
                                fontFamily = FontFamily(Font(R.font.sflightit))
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = meal.description,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily(Font(R.font.sfpro))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Cooking info
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Prep Time", fontWeight = FontWeight.Bold)
                        Text("${meal.preparationTime} min")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cook Time", fontWeight = FontWeight.Bold)
                        Text("${meal.cookingTime} min")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Servings", fontWeight = FontWeight.Bold)
                        Text("${meal.servings}")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Calories", fontWeight = FontWeight.Bold)
                        Text("${meal.caloriesPerServing}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interaction stats
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InteractionStat(label = "Views", count = meal.interactions.views)
                    InteractionStat(label = "Likes", count = meal.interactions.likes)
                    InteractionStat(label = "Shares", count = meal.interactions.shares)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ingredients
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.sf_pro_rounded_heavy))
            )

            Spacer(modifier = Modifier.height(8.dp))

            meal.ingredients.forEach { ingredient ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ${ingredient.quantity} ${ingredient.unit} ${ingredient.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Instructions
            Text(
                text = "Instructions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.sf_pro_rounded_heavy))
            )

            Spacer(modifier = Modifier.height(8.dp))

            meal.instructions.forEachIndexed { index, instruction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = instruction,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cookware
            Text(
                text = "Cookware",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.sf_pro_rounded_heavy))
            )

            Spacer(modifier = Modifier.height(8.dp))

            meal.cookware.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "• $item",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Creator info
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Profile picture if available
                        if (meal.createdBy.profilePicture != null) {
                            AsyncImage(
                                model = meal.createdBy.profilePicture,
                                contentDescription = "Profile picture of ${meal.createdBy.username}",
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp),
                                placeholder = painterResource(id = R.drawable.android_robot),
                                error = painterResource(id = R.drawable.android_robot)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column {
                            Text("Created by", style = MaterialTheme.typography.bodySmall)
                            Text(meal.createdBy.username, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row {
                        Text(
                            text = "Rating: ${meal.rating}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${meal.reviewsCount} reviews)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Notes section if there are any
            if (meal.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Notes & Comments",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.sf_pro_rounded_heavy))
                )

                Spacer(modifier = Modifier.height(8.dp))

                meal.notes.forEach { note ->
                    NoteItem(note = note)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Created/Updated dates
            Text(
                text = "Created: ${(meal.createdAt)}",
                style = MaterialTheme.typography.bodySmall
            )

            if (meal.updatedAt != meal.createdAt) {
                Text(
                    text = "Last updated: ${(meal.updatedAt)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InteractionStat(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MealDetailScreenPreview() {
     MaterialTheme {
        MealDetailScreen(
            meal = sampleMeal,
            onNavigateBack = {}
        )
    }
}