package com.example.journeysapp.data.repositories

import android.content.Context
import com.example.journeysapp.data.AppDatabase
import com.example.journeysapp.data.model.Journey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class JourneyRepository @Inject constructor(@ApplicationContext applicationContext: Context) {
    private val database = AppDatabase.builder(applicationContext).build()

    suspend fun deleteJourney(journey: Journey) = withContext(Dispatchers.IO) {
        database.journeyDao().delete(journey).also {
            Timber.d("Deleted Journey: id=$it, name=${journey.name}")
        }
    }


    suspend fun getAllJourneys(): List<Journey> = withContext(Dispatchers.IO) {
        return@withContext database.journeyDao().getAll().also {
            Timber.d("Fetched ${it.size} categories.")
        }
    }

    suspend fun insertJourney(journey: Journey) = withContext(Dispatchers.IO) {
        database.journeyDao().insert(journey).also {
            Timber.d("Inserted Journey: id=$it, name=${journey.name}")
        }
    }

    suspend fun updateJourney(journey: Journey) = withContext(Dispatchers.IO) {
        database.journeyDao().update(journey).also {
            Timber.d("Edited Journey: id=$it, name=${journey.name}")
        }
    }

    suspend fun incrementGoalProgress(journeyId: Int, amount: Int = 1) =
        withContext(Dispatchers.IO) {
            database.journeyDao().incrementGoalProgress(journeyId, amount).also {
                Timber.d("Incremented goal progress for journey with id=$journeyId")
            }
        }

    suspend fun resetGoalProgress(journeyId: Int) = withContext(Dispatchers.IO) {
        database.journeyDao().resetGoalProgress(journeyId).also {
            Timber.d("Reset goal progress for journey with id=$journeyId")
        }
    }
}