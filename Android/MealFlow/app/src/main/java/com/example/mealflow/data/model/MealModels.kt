// Location: data/model/MealModels.kt
package com.example.mealflow.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MealResponse(
    @SerialName("data")
    val data: List<Meal>
)

@Serializable
data class Meal(
    @SerialName("meal_id")
    val mealId: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("ingredients")
    val ingredients: List<Ingredient>,
    @SerialName("instructions")
    val instructions: List<String>,
    @SerialName("cookware")
    val cookware: List<String>,
    @SerialName("preparation_time")
    val preparationTime: Int,
    @SerialName("cooking_time")
    val cookingTime: Int,
    @SerialName("servings")
    val servings: Int,
    @SerialName("calories_per_serving")
    val caloriesPerServing: Int,
    @SerialName("rating")
    val rating: Double,
    @SerialName("reviews_count")
    val reviewsCount: Int,
    @SerialName("created_by")
    val createdBy: User,
    @SerialName("is_favorited")
    val isFavorited: Boolean,
    @SerialName("is_saved")
    val isSaved: Boolean,
    @SerialName("notes")
    val notes: List<Note>,
    @SerialName("interactions")
    val interactions: Interactions,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class Ingredient(
    @SerialName("name")
    val name: String,
    @SerialName("quantity")
    val quantity: Double,
    @SerialName("unit")
    val unit: String
)

@Serializable
data class User(
    @SerialName("user_id")
    val userId: String,
    @SerialName("username")
    val username: String,
    @SerialName("profile_picture")
    val profilePicture: String? = null
)

@Serializable
data class Note(
    @SerialName("note_id")
    val noteId: String,
    @SerialName("user")
    val user: User,
    @SerialName("comment")
    val comment: String,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("tags")
    val tags: List<String> = emptyList(),
    @SerialName("did_cook")
    val didCook: Boolean,
    @SerialName("likes")
    val likes: Int,
    @SerialName("dislikes")
    val dislikes: Int
)

@Serializable
data class Interactions(
    @SerialName("views")
    val views: Int,
    @SerialName("likes")
    val likes: Int,
    @SerialName("dislikes")
    val dislikes: Int,
    @SerialName("shares")
    val shares: Int
)