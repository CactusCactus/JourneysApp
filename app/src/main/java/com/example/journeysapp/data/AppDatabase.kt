package com.example.journeysapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.journeysapp.data.dao.JourneyDao
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.model.internal.JourneyIconTypeConverter

@Database(entities = [Journey::class], version = 1)
@TypeConverters(JourneyIconTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private const val APP_DATABASE_NAME = "JOURNEY_APP_DATABASE"

        fun builder(context: Context) = Room.databaseBuilder(
            context, AppDatabase::class.java, APP_DATABASE_NAME
        )
    }

    abstract fun journeyDao(): JourneyDao
}