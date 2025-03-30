package com.example.mealflow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mealflow.database.community.CommunityDao
import com.example.mealflow.database.community.GetCommunityEntity

//@Database(entities = [GetCommunityEntity::class], version = 1, exportSchema = false)

//@Database(
//    entities = [GetCommunityEntity::class],
//    version = 2,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ],
//    exportSchema = true
//)
//@Database(entities = [GetCommunityEntity::class,TokenEntity::class], version = 1, exportSchema = true)
@Database(entities = [GetCommunityEntity::class], version = 1, exportSchema = true)

@TypeConverters(Converters::class)
abstract class CommunityDatabase : RoomDatabase() {

    abstract fun communityDao(): CommunityDao
//    abstract fun tokenDao(): TokenDao

    companion object {
        @Volatile
        private var INSTANCE: CommunityDatabase? = null

        fun getDatabase(context: Context): CommunityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CommunityDatabase::class.java,
                    "community_database"
                ).fallbackToDestructiveMigration() // Prevent crash due to schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


