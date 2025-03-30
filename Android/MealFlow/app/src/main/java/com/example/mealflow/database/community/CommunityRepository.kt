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
                return@withContext emptyList() // 🔥 الحل الصحيح
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
            entities // ✅ إرجاع البيانات بعد الإدخال في Room
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // ✅ تجنب الانهيار في حالة حدوث خطأ
        }
    }
    suspend fun getCommunitiesFromDB(): List<GetCommunityEntity> {
        return communityDao.getCommunities()
    }
    suspend fun getCommunitiesFromFlow(): List<GetCommunityEntity> {
        return communityDao.getAllCommunities().first() // يحول Flow إلى List
    }

}




//class CommunityRepository(private val dao: CommunityDao, private val apiService: CommunityApiService) {
//    suspend fun fetchAndStoreCommunities() {
//        val response = apiService.fetchCommunities(tokenViewModel = TokenViewModel())
//        val entities = response.communities.map {
//            CommunityEntity(it.id.toString(), it.name, it.description, it.image.toString())
//        }
//
//        // حذف البيانات القديمة
//        dao.deleteAllCommunities()
//
//        // إدخال البيانات الجديدة
//        dao.insertCommunities(entities)
//    }
//
//    suspend fun getCommunitiesFromDB(): List<CommunityEntity> {
//        return dao.getAllCommunities()
//    }
//}
