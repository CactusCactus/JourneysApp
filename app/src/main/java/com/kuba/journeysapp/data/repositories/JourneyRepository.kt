package com.kuba.journeysapp.data.repositories

import com.kuba.journeysapp.data.dao.JourneyDao
import com.kuba.journeysapp.data.model.Journey
import com.kuba.journeysapp.data.model.internal.SortMode
import com.kuba.journeysapp.data.model.internal.sortedBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class JourneyRepository @Inject constructor(
    private val dao: JourneyDao,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend fun deleteJourney(journey: Journey) = withContext(Dispatchers.IO) {
        dao.delete(journey).also {
            Timber.d("Deleted Journey: id=$it, name=${journey.name}")
        }
    }

    suspend fun getJourneyFlow(journeyId: Long) = withContext(Dispatchers.IO) {
        return@withContext dao.getAsFlow(journeyId)
    }


    suspend fun getAllJourneys(): List<Journey> = withContext(Dispatchers.IO) {
        return@withContext dao.getAll().also {
            Timber.d("Fetched ${it.size} journeys.")
        }
    }

    fun getAllJourneysSorted(): Flow<List<Journey>> {
        val journeysFlow: Flow<List<Journey>> = dao.getAllAsFlow()
        val sortModeFlow: Flow<SortMode> = userPreferencesRepository.getSortModeFlow()

        return combine(journeysFlow, sortModeFlow) { journeys, currentSortMode ->
            journeys.sortedBy(currentSortMode)
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

    suspend fun incrementGoalProgress(journeyId: Long, amount: Int = 1): Int =
        withContext(Dispatchers.IO) {
            dao.incrementGoalProgress(journeyId, amount).also {
                Timber.d("Incremented goal progress for $it journeys with id=$journeyId")
            }
        }

    suspend fun incrementGoalProgressBatch(batchUpdate: Map<Long, Int>) =
        withContext(Dispatchers.IO) {
            if (batchUpdate.isEmpty()) {
                Timber.e("Batch update is empty")
                return@withContext
            }

            batchUpdate.forEach { (journeyId, amount) ->
                dao.incrementGoalProgress(journeyId, amount)
            }

            Timber.d("Incremented goal progress for ${batchUpdate.size} journeys")
        }

    suspend fun decrementGoalProgress(journeyId: Long, amount: Int = 1): Int =
        withContext(Dispatchers.IO)
        {
            dao.decrementGoalProgress(journeyId, amount).also {
                Timber.d("Decremented goal progress for $it journeys with id=$journeyId")
            }
        }

    suspend fun resetGoalProgress(journeyId: Long) = withContext(Dispatchers.IO) {
        dao.resetGoalProgress(journeyId).also {
            Timber.d("Reset goal progress for $it journeys with id=$journeyId")
        }
    }
}