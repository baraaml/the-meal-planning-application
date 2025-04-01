package com.example.mealflow.database.community

import androidx.room.TypeConverters
import com.example.mealflow.database.Converters
import com.example.mealflow.network.CommunityApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@TypeConverters(Converters::class)

class CommunityRepository(
    private val communityDao: CommunityDao,
    private val apiService: CommunityApiService,
) {
    suspend fun fetchCommunities(): List<GetCommunityEntity> = withContext(Dispatchers.IO) {
        try {
            val response = try {
                apiService.fetchCommunities()
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext emptyList()
            }

            val communities: List<GetCommunityEntity> = response.communities
            val entities: List<GetCommunityEntity> = communities.map { community ->
                GetCommunityEntity(
                    id = community.id,
                    name = community.name,
                    description = community.description,
                    image = community.image ?: "",
                    privacy = community.privacy,
                    recipeCreationPermission = community.recipeCreationPermission,
                    createdAt = community.createdAt,
                    updatedAt = community.updatedAt,
                    categories = community.categories,
                    members = community.members,
                    owner = community.owner
                )
            }

            communityDao.deleteAllCommunities()
            communityDao.insertCommunities(entities)
            entities // ✅ Return data after entering into Room
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // ✅ Avoid crashing in case of an error
        }
    }
    suspend fun getCommunitiesFromDB(): List<GetCommunityEntity> {
        return communityDao.getCommunities()
    }
    suspend fun getCommunitiesFromFlow(): List<GetCommunityEntity> {
        return communityDao.getAllCommunities().first() // Converts Flow to List
    }

}