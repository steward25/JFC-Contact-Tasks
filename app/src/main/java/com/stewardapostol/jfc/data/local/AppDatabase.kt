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
        PersonTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_db").build().also { INSTANCE = it }
        }
    }
}

