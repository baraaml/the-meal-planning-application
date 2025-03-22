package com.example.mealflow.data.samples

import com.example.mealflow.data.model.Ingredient
import com.example.mealflow.data.model.Interactions
import com.example.mealflow.data.model.Meal
import com.example.mealflow.data.model.Note
import com.example.mealflow.data.model.User

val sampleMeal = Meal(
    mealId = "meal123",
    name = "Creamy Mushroom Pasta",
    description = "A delicious pasta dish with a rich mushroom sauce, perfect for a cozy dinner.",
    imageUrl = "", // Empty URL will show placeholder
    tags = listOf("Italian", "Vegetarian", "Dinner"),
    ingredients = listOf(
        Ingredient("Fettuccine pasta", 250.0, "g"),
        Ingredient("Mushrooms", 250.0, "g"),
        Ingredient("Heavy cream", 250.0, "ml"),
        Ingredient("Garlic",250.0, "cloves"),
        Ingredient("Parmesan cheese",250.0, "g")
    ),
    instructions = listOf(
        "Cook pasta according to package instructions.",
        "Sauté mushrooms and garlic in olive oil until golden brown.",
        "Add heavy cream and simmer for 5 minutes.",
        "Combine with cooked pasta and top with grated parmesan."
    ),
    cookware = listOf("Large pot", "Frying pan", "Cutting board", "Knife"),
    preparationTime = 10,
    cookingTime = 20,
    servings = 4,
    caloriesPerServing = 450,
    rating = 4.7,
    reviewsCount = 152,
    createdBy = User(
        userId = "user456",
        username = "ChefJulia",
        profilePicture = null
    ),
    isFavorited = true,
    isSaved = false,
    notes = listOf(
        Note(
            noteId = "note789",
            user = User(
                userId = "user321",
                username = "FoodLover42",
                profilePicture = null
            ),
            comment = "I made this last night and it was amazing! I added some spinach too.",
            imageUrl = null,
            didCook = true,
            likes = 24,
            dislikes = 0,
            tags = emptyList()
        )
    ),
    interactions = Interactions(
        views = 3542,
        likes = 286,
        dislikes = 5,
        shares = 47
    ),
    createdAt = "2024-03-15T14:30:00Z",
    updatedAt = "2024-03-18T09:15:30Z"
)