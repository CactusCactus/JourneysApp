package com.example.journeysapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.journeysapp.data.dao.JourneyDao
import com.example.journeysapp.data.model.Journey

@Database(entities = [Journey::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun journeyDao(): JourneyDao
}