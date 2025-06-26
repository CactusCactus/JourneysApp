package com.kuba.journeysapp.data.repositories

import com.kuba.journeysapp.data.dao.GoalHistoryDao
import com.kuba.journeysapp.data.model.GoalHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class GoalHistoryRepository @Inject constructor(private val dao: GoalHistoryDao) {
    suspend fun getGoalHistory(): List<GoalHistory> = withContext(Dispatchers.IO) {
        return@withContext dao.getAll().also {
            Timber.d("Fetched goal history of size ${it.size}")
        }
    }

    suspend fun getGoalHistory(journeyId: Long): List<GoalHistory> =
        withContext(Dispatchers.IO) {
            return@withContext dao.getHistoryForJourney(journeyId).also {
                Timber.d("Fetched goal history of size ${it.size} for flow for journey with id=$journeyId")
            }
        }

    suspend fun getGoalHistoryFlow(journeyId: Long): Flow<List<GoalHistory>> =
        withContext(Dispatchers.IO) {
            return@withContext dao.getHistoryForJourneyAsFlow(journeyId).also {
                Timber.d("Fetched goal history flow for journey with id=$journeyId")
            }
        }
}