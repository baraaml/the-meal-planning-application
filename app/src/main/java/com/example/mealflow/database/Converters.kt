package com.example.mealflow.database

import androidx.room.TypeConverter
import com.example.mealflow.network.Category
import com.example.mealflow.network.Member
import com.example.mealflow.network.OwnerCommunity
import com.example.mealflow.network.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCategoryList(categories: List<Category>): String {
        return gson.toJson(categories)
    }

    @TypeConverter
    fun toCategoryList(data: String): List<Category> {
        if (data.isEmpty()) return emptyList()
        val listType = object : TypeToken<List<Category>>() {}.type
        return gson.fromJson(data, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromMemberList(members: List<Member>): String {
        return gson.toJson(members)
    }

    @TypeConverter
    fun toMemberList(data: String): List<Member> {
        val listType = object : TypeToken<List<Member>>() {}.type
        return gson.fromJson(data, listType)
    }
    @TypeConverter
    fun fromOwnerCommunity(owner: OwnerCommunity): String {
        return gson.toJson(owner)
    }

    @TypeConverter
    fun toOwnerCommunity(data: String): OwnerCommunity {
        return gson.fromJson(data, OwnerCommunity::class.java)
    }

    @TypeConverter
    fun fromUser(user: User): String {
        return Gson().toJson(user)
    }

    @TypeConverter
    fun toUser(userJson: String): User {
        return Gson().fromJson(userJson, User::class.java)
    }

}
