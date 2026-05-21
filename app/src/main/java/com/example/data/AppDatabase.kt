package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavProduct::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val favoritesDao: FavoritesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = try {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "ticia_jeans_db_v3"
                    ).fallbackToDestructiveMigration().build()
                } catch (e: Exception) {
                    android.util.Log.e("AppDatabase", "SQLite file db creation failed, fallback to in-memory database", e)
                    Room.inMemoryDatabaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java
                    ).fallbackToDestructiveMigration().build()
                }
                INSTANCE = instance
                instance
            }
        }
    }
}
