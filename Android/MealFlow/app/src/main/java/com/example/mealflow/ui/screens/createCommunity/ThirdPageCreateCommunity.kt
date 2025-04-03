package com.example.mealflow.ui.screens.createCommunity

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.ui.components.SelectableTopicCard
import com.example.mealflow.ui.components.TopBarCreateCommunity
import com.example.mealflow.viewModel.CommunityViewModel


@Composable
fun ThirdStep(
    navController: NavController,
    viewModel: CommunityViewModel = viewModel()
) {
    val communityName by viewModel.communityName.observeAsState("")
    val selectedTopics = remember { mutableStateListOf<String>() }
    val categoriesCommunity by viewModel.categories.observeAsState(emptyList())

    // Available categories and topics

    val categories = getCategories()

    Scaffold(
        topBar = {
            TopBarCreateCommunity(
                navController = navController,
                textNumber = "3 of 4",
                onClick = { navController.navigate("FourthStep Page") },
                 true
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.navigationBarsPadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .systemBarsPadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Enable vertical scrolling
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose community topics",
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display topics horizontally with scrolling
            for ((category, topics) in categories) {
                Text(
                    text = category,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()), // Horizontal scrolling
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between topics
                ) {
                    for (topic in topics) {
                        SelectableTopicCard(
                            topic = topic,
                            isSelected = topic in selectedTopics,
                            onSelected = { selectedTopic ->
                                if (selectedTopics.contains(selectedTopic)) {
                                    selectedTopics.remove(selectedTopic)
                                    viewModel.updateCategories(selectedTopics)
                                } else {
                                    if (selectedTopics.size < 3) {
                                        selectedTopics.add(selectedTopic)
                                        viewModel.updateCategories(selectedTopics)
                                    }
                                }
                            },
                            modifier = Modifier
                                .background(
                                    color = if (topic in selectedTopics) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp)) // Add shadow for modern look
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Selected Topics: ${selectedTopics.joinToString(", ")}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewThirdStep() {
    ThirdStep(navController = rememberNavController())
}


fun getCategories(): Map<String, List<String>> {
    return mapOf(
        "Cuisine Types" to listOf(
            "Italian Cuisine", "French Cuisine", "Mexican Cuisine", "Middle Eastern Cuisine",
            "Asian Cuisine", "American Cuisine", "Mediterranean Cuisine", "Indian Cuisine", "African Cuisine"
        ),
        "Dietary Preferences" to listOf(
            "Vegan & Plant-Based", "Vegetarian", "Keto & Low-Carb", "Paleo", "Gluten-Free",
            "Dairy-Free", "High-Protein", "Halal & Kosher"
        ),
        "Meal Types" to listOf(
            "Breakfast & Brunch", "Lunch & Quick Meals", "Dinner & Main Courses",
            "Snacks & Appetizers", "Desserts & Sweets"
        ),
        "Cooking Styles & Techniques" to listOf(
            "Home Cooking", "Baking & Pastry", "Grilling & BBQ", "Slow Cooking & Crockpot",
            "Air Fryer Recipes", "One-Pot & Instant Pot Meals", "Fermentation & Pickling"
        ),
        "Special Occasions & Seasonal Meals" to listOf(
            "Holiday & Festive Meals", "Ramadan & Iftar", "Summer Refreshing Meals",
            "Winter Comfort Foods", "Birthday & Celebration Cakes"
        ),
        "Health & Wellness" to listOf(
            "Weight Loss Recipes", "Muscle Gain & Fitness Meals", "Diabetic-Friendly Meals",
            "Heart-Healthy Recipes"
        ),
        "Budget & Time-Friendly Meals" to listOf(
            "5-Ingredient Recipes", "30-Minute Meals", "Cheap & Budget-Friendly Meals",
            "Meal Prepping & Batch Cooking"
        ),
        "Cultural & Regional Foods" to listOf(
            "Street Food", "Traditional & Ancestral Recipes", "Fusion Recipes"
        ),
        "Drinks & Beverages" to listOf(
            "Coffee & Tea Lovers", "Smoothies & Juices", "Cocktails & Mocktails",
            "Homemade Brews & Fermented Drinks", "Detox & Wellness Drinks"
        ),
        "Kids & Family Meals" to listOf(
            "Kid-Friendly Recipes", "School Lunch Ideas", "Family Dinners",
            "Fun & Creative Snacks", "Baby Food & Toddler Meals"
        ),
        "Sustainability & Zero Waste" to listOf(
            "Zero-Waste Cooking", "Seasonal & Local Produce", "Nose-to-Tail Cooking",
            "Root-to-Stem Recipes", "Sustainable Seafood"
        ),
        "Global Street Food" to listOf(
            "Asian Street Food", "Latin American Street Food", "European Street Food",
            "African Street Food", "Middle Eastern Street Food"
        ),
        "Food Allergies & Intolerances" to listOf(
            "Nut-Free Recipes", "Egg-Free Recipes", "Soy-Free Recipes",
            "Shellfish-Free Recipes", "Low-FODMAP Recipes"
        ),
        "Cooking for One or Two" to listOf(
            "Single-Serving Recipes", "Date Night Meals", "Small-Batch Baking",
            "Minimalist Cooking"
        ),
        "Food Science & Experimentation" to listOf(
            "Molecular Gastronomy", "Sous Vide Cooking", "Food Pairing & Flavor Science",
            "DIY Kitchen Experiments"
        ),
        "Food Challenges & Trends" to listOf(
            "Viral Food Trends", "Spicy Food Challenges", "Retro & Nostalgic Recipes",
            "Food Art & Aesthetics", "Extreme Food Challenges"
        ),
        "Food History & Culture" to listOf(
            "Ancient Recipes", "Historical Dishes", "Indigenous Cuisine",
            "Culinary Traditions", "Food Anthropology"
        ),
        "Food Photography & Styling" to listOf(
            "Food Styling Tips", "Flat Lay Photography", "Recipe Video Creation",
            "Food Blogging & Content Creation"
        ),
        "Food Pairing & Wine" to listOf(
            "Wine & Cheese Pairings", "Beer & Food Pairings", "Non-Alcoholic Pairings",
            "Dessert & Drink Combos"
        ),
        "Food Hacks & Tips" to listOf(
            "Kitchen Hacks", "Ingredient Substitutions", "Time-Saving Tips",
            "Leftover Makeovers", "Meal Planning Strategies"
        ),
        "Food Travel & Exploration" to listOf(
            "Foodie Travel Guides", "Regional Food Adventures", "Food Tours & Experiences",
            "International Grocery Hauls"
        ),
        "Food for Mental Health" to listOf(
            "Mood-Boosting Recipes", "Stress-Relief Foods", "Brain-Boosting Meals",
            "Comfort Foods for Tough Days"
        ),
        "Food for Athletes" to listOf(
            "Pre-Workout Meals", "Post-Workout Recovery", "Endurance Fueling",
            "Hydration & Electrolytes"
        ),
        "Food for Pets" to listOf(
            "Homemade Dog Treats", "Cat-Friendly Recipes", "Healthy Pet Meals",
            "DIY Pet Food"
        ),
        "Food for Special Diets" to listOf(
            "Low-Sodium Recipes", "Low-Sugar Recipes", "Anti-Inflammatory Meals",
            "Autoimmune Protocol (AIP)"
        )
    )
}
