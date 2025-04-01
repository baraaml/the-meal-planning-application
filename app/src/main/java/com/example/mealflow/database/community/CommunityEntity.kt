package com.example.mealflow.database.community

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mealflow.database.Converters
import com.example.mealflow.network.Category
import com.example.mealflow.network.Member
import com.example.mealflow.network.OwnerCommunity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "communities")
@TypeConverters(Converters::class)
data class GetCommunityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val image: String?,
    val privacy: String,
    val recipeCreationPermission: String,
    val createdAt: String,
    val updatedAt: String,
    val categories: List<Category>,
    val members: List<Member>,
    @Embedded(prefix = "owner_") val owner: OwnerCommunity // ✅ Repetition solution
)



