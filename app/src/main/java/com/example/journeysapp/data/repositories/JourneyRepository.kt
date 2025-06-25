package com.example.journeysapp.data.repositories

import com.example.journeysapp.data.dao.JourneyDao
import com.example.journeysapp.data.model.Journey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class JourneyRepository @Inject constructor(private val dao: JourneyDao) {
    suspend fun deleteJourney(journey: Journey) = withContext(Dispatchers.IO) {
        dao.delete(journey).also {
            Timber.d("Deleted Journey: id=$it, name=${journey.name}")
        }
    }

    suspend fun getJourney(journeyId: Long) = withContext(Dispatchers.IO) {
        return@withContext dao.get(journeyId)
    }


    suspend fun getAllJourneys(): List<Journey> = withContext(Dispatchers.IO) {
        return@withContext dao.getAll().also {
            Timber.d("Fetched ${it.size} categories.")
        }
    }

    suspend fun insertJourney(journey: Journey): Long = withContext(Dispatchers.IO) {
        dao.insert(journey).also {
            Timber.d("Inserted Journey: id=$it, name=${journey.name}")
        }
    }

    suspend fun updateJourney(journey: Journey) = withContext(Dispatchers.IO) {
        dao.update(journey).also {
            Timber.d("Edited Journey: id=$it, name=${journey.name}")
        }
    }

    suspend fun incrementGoalProgress(journeyId: Long, amount: Int = 1) : Int =
        withContext(Dispatchers.IO) {
            dao.incrementGoalProgress(journeyId, amount).also {
                Timber.d("Incremented goal progress for $it journeys with id=$journeyId")
            }
        }

    suspend fun resetGoalProgress(journeyId: Long) = withContext(Dispatchers.IO) {
        dao.resetGoalProgress(journeyId).also {
            Timber.d("Reset goal progress for $it journeys with id=$journeyId")
        }
    }
}