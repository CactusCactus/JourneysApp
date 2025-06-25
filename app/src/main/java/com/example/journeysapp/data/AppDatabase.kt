package com.example.journeysapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.journeysapp.BuildConfig
import com.example.journeysapp.data.dao.GoalHistoryDao
import com.example.journeysapp.data.dao.JourneyDao
import com.example.journeysapp.data.model.DateConverter
import com.example.journeysapp.data.model.GoalFrequencyTypeConverter
import com.example.journeysapp.data.model.GoalHistory
import com.example.journeysapp.data.model.GoalTypeConverter
import com.example.journeysapp.data.model.Journey
import com.example.journeysapp.data.model.internal.JourneyIconTypeConverter

@Database(entities = [Journey::class, GoalHistory::class], version = 1)
@TypeConverters(
    JourneyIconTypeConverter::class,
    GoalFrequencyTypeConverter::class,
    GoalTypeConverter::class,
    DateConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private const val APP_DATABASE_NAME = "JOURNEY_APP_DATABASE"

        fun builder(context: Context) = Room.databaseBuilder(
            context, AppDatabase::class.java, APP_DATABASE_NAME
        ).also {
            if (BuildConfig.DEBUG) {
                it.fallbackToDestructiveMigration(true)
            }
        }
    }

    abstract fun journeyDao(): JourneyDao

    abstract fun goalHistoryDao(): GoalHistoryDao
}