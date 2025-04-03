//package com.example.mealflow.ui.screens
//
//import android.util.Log
//import androidx.compose.foundation.border
//import androidx.compose.foundation.horizontalScroll
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.navigationBarsPadding
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.systemBarsPadding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Snackbar
//import androidx.compose.material3.SnackbarHost
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.example.mealflow.R
//import com.example.mealflow.database.token.TokenManager
//import com.example.mealflow.network.createCommunityApi
//import com.example.mealflow.ui.components.CardCommunityTest
//import com.example.mealflow.ui.components.ImagePickerSection
//import com.example.mealflow.ui.components.PrivacySettingsScreen
//import com.example.mealflow.ui.components.SelectableTopicCard
//import com.example.mealflow.ui.components.TopBarCreateCommunity
//import com.example.mealflow.viewModel.CommunityViewModel
//
//@Composable
//fun FirstStep(navController: NavController, viewModel: CommunityViewModel = viewModel()) {
//    val communityName by viewModel.communityName.observeAsState("")
//    val description by viewModel.communityDescription.observeAsState("")
//
//    val isButtonEnabled = communityName.isNotBlank() && description.length >= 15
//
//    Scaffold(
//        topBar = {
//            TopBarCreateCommunity(
//                navController = navController,
//                textNumber = "1 of 4",
//                onClick = { if (isButtonEnabled) navController.navigate("SecondStep Page") },
//                isButtonEnabled = isButtonEnabled
//            )
//        },
//        bottomBar = {
////            BottomBar(navController)
//        },
//        containerColor = Color(0xFFEFEFEF),
//        modifier = Modifier.navigationBarsPadding()
//    )
//    { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .navigationBarsPadding()
//                .systemBarsPadding()
//                .padding(paddingValues)
//        ) {
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = stringResource(id = R.string.CreateCommunityHeaderFirstPage),
//                modifier = Modifier.padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 35.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            Text(
//                text = "Community Name *",
//                color = Color.Gray,
//                fontWeight = FontWeight.SemiBold
//            )
//
//            OutlinedTextField(
//                value = communityName,
//                onValueChange = { viewModel.updateCommunityName(it) },
//                modifier = Modifier.fillMaxWidth(),
//                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
//                label = { Text("r/Community") }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text("Description *", color = Color.Gray, fontWeight = FontWeight.SemiBold)
//            OutlinedTextField(
//                value = description,
//                onValueChange = { viewModel.updateCommunityDescription(it) },
//                modifier = Modifier.fillMaxWidth(),
//                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
//                label = { Text("Enter description") }
//            )
//        }
//    }
//}
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewFirstStepCreateCommunity() {
//    FirstStep(navController = rememberNavController())
//}
//
//
//
//@Composable
//fun SecondStep(navController: NavController, viewModel: CommunityViewModel = viewModel())
//{
//    val communityName by viewModel.communityName.observeAsState("")
//    var description by remember { mutableStateOf("test") }
//    val selectedImageUri = viewModel.selectedImageUri
//
//    // Activate the button based on the image selection
//    val isButtonEnabled = selectedImageUri != null
//
//    Scaffold(
//        topBar = {
//            TopBarCreateCommunity(navController = navController,"2 of 4",{navController.navigate("ThirdStep Page")},isButtonEnabled)
//        },
//        bottomBar = {
////            BottomBar(navController)
//        },
//        containerColor = Color(0xFFEFEFEF),
//        modifier = Modifier.navigationBarsPadding()
//    )
//    { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .navigationBarsPadding()
//                .systemBarsPadding() // ✅ Adds space so that the content doesn't overlap the notification bar
//                .padding(paddingValues)
//        ) {
//            Spacer(modifier = Modifier.height(8.dp)) // Small space between the button and the text
//            Text(
//                text = stringResource(id = R.string.CreateCommunityHeaderSecondPage),
//                modifier = Modifier
//                    .padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 35.sp
//            )
//            Text(
//                text = stringResource(id = R.string.CreateCommunitySubHeaderSecondPage),
//                modifier = Modifier
//                    .padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 15.sp
//            )
//            Spacer(modifier = Modifier.height(5.dp)) // Small space between the button and the text
//            Text(
//                text = stringResource(id = R.string.Preview),
//                modifier = Modifier
//                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
//                    .padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 20.sp
//            )
//            CardCommunityTest(communityName = communityName, members = 1000, recipes = 50,viewModel.selectedImageUri)
//            ImagePickerSection(viewModel)
//        }
//    }
//}
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewSecondStep() {
//    SecondStep(navController = rememberNavController())
//}
//
//@Composable
//fun ThirdStep(
//    navController: NavController,
//    viewModel: CommunityViewModel = viewModel()
//) {
//    val communityName by viewModel.communityName.observeAsState("")
//    val selectedTopics = remember { mutableStateListOf<String>() }
//    val categoriesCommunity by viewModel.categories.observeAsState(emptyList())
//
//    // Available categories and topics
//    // Available categories and topics based on new data
//    val categories = mapOf(
//        "Cuisine Types" to listOf(
//            "Italian Cuisine", "French Cuisine", "Mexican Cuisine", "Middle Eastern Cuisine",
//            "Asian Cuisine", "American Cuisine", "Mediterranean Cuisine", "Indian Cuisine", "African Cuisine"
//        ),
//        "Dietary Preferences" to listOf(
//            "Vegan & Plant-Based", "Vegetarian", "Keto & Low-Carb", "Paleo", "Gluten-Free",
//            "Dairy-Free", "High-Protein", "Halal & Kosher"
//        ),
//        "Meal Types" to listOf(
//            "Breakfast & Brunch", "Lunch & Quick Meals", "Dinner & Main Courses",
//            "Snacks & Appetizers", "Desserts & Sweets"
//        ),
//        "Cooking Styles & Techniques" to listOf(
//            "Home Cooking", "Baking & Pastry", "Grilling & BBQ", "Slow Cooking & Crockpot",
//            "Air Fryer Recipes", "One-Pot & Instant Pot Meals", "Fermentation & Pickling"
//        ),
//        "Special Occasions & Seasonal Meals" to listOf(
//            "Holiday & Festive Meals", "Ramadan & Iftar", "Summer Refreshing Meals",
//            "Winter Comfort Foods", "Birthday & Celebration Cakes"
//        ),
//        "Health & Wellness" to listOf(
//            "Weight Loss Recipes", "Muscle Gain & Fitness Meals", "Diabetic-Friendly Meals",
//            "Heart-Healthy Recipes"
//        ),
//        "Budget & Time-Friendly Meals" to listOf(
//            "5-Ingredient Recipes", "30-Minute Meals", "Cheap & Budget-Friendly Meals",
//            "Meal Prepping & Batch Cooking"
//        ),
//        "Cultural & Regional Foods" to listOf(
//            "Street Food", "Traditional & Ancestral Recipes", "Fusion Recipes"
//        ),
//        "Drinks & Beverages" to listOf(
//            "Coffee & Tea Lovers", "Smoothies & Juices", "Cocktails & Mocktails",
//            "Homemade Brews & Fermented Drinks", "Detox & Wellness Drinks"
//        ),
//        "Kids & Family Meals" to listOf(
//            "Kid-Friendly Recipes", "School Lunch Ideas", "Family Dinners",
//            "Fun & Creative Snacks", "Baby Food & Toddler Meals"
//        ),
//        "Sustainability & Zero Waste" to listOf(
//            "Zero-Waste Cooking", "Seasonal & Local Produce", "Nose-to-Tail Cooking",
//            "Root-to-Stem Recipes", "Sustainable Seafood"
//        ),
//        "Global Street Food" to listOf(
//            "Asian Street Food", "Latin American Street Food", "European Street Food",
//            "African Street Food", "Middle Eastern Street Food"
//        ),
//        "Food Allergies & Intolerances" to listOf(
//            "Nut-Free Recipes", "Egg-Free Recipes", "Soy-Free Recipes",
//            "Shellfish-Free Recipes", "Low-FODMAP Recipes"
//        ),
//        "Cooking for One or Two" to listOf(
//            "Single-Serving Recipes", "Date Night Meals", "Small-Batch Baking",
//            "Minimalist Cooking"
//        ),
//        "Food Science & Experimentation" to listOf(
//            "Molecular Gastronomy", "Sous Vide Cooking", "Food Pairing & Flavor Science",
//            "DIY Kitchen Experiments"
//        ),
//        "Food Challenges & Trends" to listOf(
//            "Viral Food Trends", "Spicy Food Challenges", "Retro & Nostalgic Recipes",
//            "Food Art & Aesthetics", "Extreme Food Challenges"
//        ),
//        "Food History & Culture" to listOf(
//            "Ancient Recipes", "Historical Dishes", "Indigenous Cuisine",
//            "Culinary Traditions", "Food Anthropology"
//        ),
//        "Food Photography & Styling" to listOf(
//            "Food Styling Tips", "Flat Lay Photography", "Recipe Video Creation",
//            "Food Blogging & Content Creation"
//        ),
//        "Food Pairing & Wine" to listOf(
//            "Wine & Cheese Pairings", "Beer & Food Pairings", "Non-Alcoholic Pairings",
//            "Dessert & Drink Combos"
//        ),
//        "Food Hacks & Tips" to listOf(
//            "Kitchen Hacks", "Ingredient Substitutions", "Time-Saving Tips",
//            "Leftover Makeovers", "Meal Planning Strategies"
//        ),
//        "Food Travel & Exploration" to listOf(
//            "Foodie Travel Guides", "Regional Food Adventures", "Food Tours & Experiences",
//            "International Grocery Hauls"
//        ),
//        "Food for Mental Health" to listOf(
//            "Mood-Boosting Recipes", "Stress-Relief Foods", "Brain-Boosting Meals",
//            "Comfort Foods for Tough Days"
//        ),
//        "Food for Athletes" to listOf(
//            "Pre-Workout Meals", "Post-Workout Recovery", "Endurance Fueling",
//            "Hydration & Electrolytes"
//        ),
//        "Food for Pets" to listOf(
//            "Homemade Dog Treats", "Cat-Friendly Recipes", "Healthy Pet Meals",
//            "DIY Pet Food"
//        ),
//        "Food for Special Diets" to listOf(
//            "Low-Sodium Recipes", "Low-Sugar Recipes", "Anti-Inflammatory Meals",
//            "Autoimmune Protocol (AIP)"
//        )
//    )
//
//    Scaffold(
//        topBar = {TopBarCreateCommunity(navController = navController, "3 of 4", {navController.navigate("FourthStep Page")}, true) },
//        bottomBar = {
////            BottomBar(navController)
//                    },
//        containerColor = Color(0xFFEFEFEF),
//        modifier = Modifier.navigationBarsPadding()
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .systemBarsPadding()
//                .padding(16.dp)
//                .verticalScroll(rememberScrollState()) // ✅ Activate vertical scrolling
//        ) {
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "Choose community topics",
//                modifier = Modifier.padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 30.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
////            // عرض المواضيع بشكل رأسي مع قابلية التمرير
////            for ((category, topics) in categories) {
////                Text(
////                    text = category,
////                    fontSize = 20.sp,
////                    fontWeight = FontWeight.SemiBold,
////                    modifier = Modifier.padding(vertical = 8.dp)
////                )
////
////                // عرض المواضيع بشكل رأسي مباشر بدلًا من LazyVerticalGrid
////                for (topic in topics) {
////                    SelectableTopicCard(
////                        topic = topic,
////                        isSelected = topic in selectedTopics,
////                        onSelected = { selectedTopic ->
////                            if (selectedTopics.contains(selectedTopic)) {
////                                selectedTopics.remove(selectedTopic)
////                            } else {
////                                if (selectedTopics.size < 3) {
////                                    selectedTopics.add(selectedTopic)
////                                }
////                            }
////                        }
////                    )
////                }
////            }
//            // Display topics horizontally with scrolling
//            for ((category, topics) in categories) {
//                Text(
//                    text = category,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    modifier = Modifier.padding(vertical = 8.dp)
//                )
//
//                // Use Row to display topics side by side
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .horizontalScroll(rememberScrollState()), // Horizontal scrolling when topics increase
//                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between topics
//                ) {
//                    for (topic in topics) {
//                        SelectableTopicCard(
//                            topic = topic,
//                            isSelected = topic in selectedTopics,
//                            onSelected = { selectedTopic ->
//                                if (selectedTopics.contains(selectedTopic)) {
//                                    selectedTopics.remove(selectedTopic)
//                                    viewModel.updateCategories(selectedTopics)
//                                } else {
//                                    if (selectedTopics.size < 3) {
//                                        selectedTopics.add(selectedTopic)
//                                        viewModel.updateCategories(selectedTopics)
//                                    }
//                                }
//                            }
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "Selected Topics: ${selectedTopics.joinToString(", ")}",
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Medium
//            )
//        }
//    }
//}
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewThirdStep() {
//    ThirdStep(navController = rememberNavController())
//}
//
//@Composable
//fun FourthStep(
//    navController: NavController,
//    viewModel: CommunityViewModel = viewModel(),
//) {
//    // Existing state observations
//    val communityName = viewModel.communityName.observeAsState("").value
//    val communityDescription = viewModel.communityDescription.observeAsState("").value
//    val recipeCreationPermission = viewModel.recipeCreationPermission.observeAsState("").value
////    val accessToken = viewModelToken.accessToken.observeAsState("").value
//    val categories = viewModel.categories.observeAsState(emptyList()).value
//    val selectedImageUri = viewModel.selectedImageUri
//    // Create SnackbarHostState
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    val context = LocalContext.current
//    val tokenManager = TokenManager(context)
//    val accessToken = tokenManager.getAccessToken()
//
//
//    val isButtonEnabled = communityName.isNotBlank() &&
//            communityDescription.isNotBlank()
////            &&
////            accessToken.isNotBlank()
//    // Wrap the entire content in a Scaffold to ensure SnackbarHost is visible
//    Scaffold(
//        topBar =
//        {
//            TopBarCreateCommunity(
//                navController = navController,
//                "4 of 4",
//                {
//                    // Log for debugging
//                    Log.d("FourthStep", "Creating Community")
//                    Log.d("FourthStep", "Name: $communityName")
//                    Log.d("FourthStep", "Description: $communityDescription")
//                    Log.d("FourthStep", "Token: $accessToken")
//                    Log.d("FourthStep", "Image URI: $selectedImageUri")
//
//                    accessToken?.let {
//                        createCommunityApi(
//                            context,
//                            communityName,
//                            communityDescription,
//                            recipeCreationPermission,
//                            it,
//                            categories,
//                            selectedImageUri,
//                            navController = navController,
//                            snackbarHostState = snackbarHostState
//                        )
//                    }
//                },
//                isButtonEnabled
//            )
//        },
//        snackbarHost = {
//            SnackbarHost(hostState = snackbarHostState) { data ->
//                Snackbar(
//                    snackbarData = data,
//                    modifier = Modifier.padding(16.dp)
//                )
//            }
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .navigationBarsPadding()
//                .systemBarsPadding()
//                .padding(paddingValues) // Use the padding from Scaffold
//                .padding(16.dp)
//        ) {
//            Spacer(modifier = Modifier.height(8.dp)) // Small space between the button and the text
//            Text(
//                text = "Select community type",
//                modifier = Modifier
//                    .padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 35.sp
//            )
//            Text(
//                text = "Decide who can view and contribute in your community. Only public communities show up in search. Important: Once set, you can only change your community type with Reddit's approval.",
//                modifier = Modifier
//                    .padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 15.sp
//            )
//            Spacer(modifier = Modifier.height(5.dp)) // Small space between the button and the text
//            PrivacySettingsScreen(viewModel)
//        }
//    }
//}
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewFourthStep() {
//    FourthStep(navController = rememberNavController())
//}