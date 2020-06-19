package com.example.coroutines.app

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coroutines.main.data.Dog
import com.example.coroutines.main.data.DogDao

@Database(entities = [Dog::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME: String = "app_db"
    }

    abstract fun getDogDao(): DogDao
}