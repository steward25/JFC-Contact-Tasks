package com.stewardapostol.jfc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Business::class,
        Person::class,
        Task::class,
        Category::class,
        Tag::class,
        BusinessCategoryCrossRef::class,
        BusinessTagCrossRef::class,
        PersonTagCrossRef::class // Ensure this matches your latest data class name
    ],
    version = 3, // Increment version from 1 to 2
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .fallbackToDestructiveMigration() // Recommended during development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}