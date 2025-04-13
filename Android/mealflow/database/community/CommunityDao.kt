package com.example.mealflow.database.community

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunities(communities: List<GetCommunityEntity>)

    @Query("SELECT * FROM communities")
    fun getAllCommunities(): Flow<List<GetCommunityEntity>>

    @Query("DELETE FROM communities")
    suspend fun deleteAllCommunities()

    @Query("SELECT * FROM communities")
    suspend fun getCommunities(): List<GetCommunityEntity>

    @Query("SELECT * FROM communities ")
    suspend fun getCommunityId(): List<GetCommunityEntity>

    @Query("SELECT * FROM communities WHERE id = :communityId")
    suspend fun getCommunityWithID(communityId: String): GetCommunityEntity?

}