package com.kuba.journeysapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kuba.journeysapp.data.dao.GoalHistoryDao
import com.kuba.journeysapp.data.dao.JourneyDao
import com.kuba.journeysapp.data.model.GoalFrequency
import com.kuba.journeysapp.data.model.GoalHistory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.Date

@HiltWorker
class ResetGoalProgressWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val journeyDao: JourneyDao,
    private val goalHistoryDao: GoalHistoryDao
) : CoroutineWorker(appContext, params) {
    companion object {
        const val KEY_FREQUENCY = "frequency"

        fun getUniqueWorkName(frequency: GoalFrequency) = "${frequency.name}_GoalResetWork"
    }

    override suspend fun doWork(): Result {
        val frequency: GoalFrequency =
            inputData.getString(KEY_FREQUENCY)?.let {
                GoalFrequency.valueOf(it)
            } ?: return Result.failure().also {
                Timber.e("No frequency provided to ResetGoalProgressWorker.")
            }

        return try {
            Timber.d("Starting to reset goals of frequency ${frequency.name} progress work.")
            val journeysToReset = journeyDao.getAllWithFrequency(frequency)

            journeysToReset.forEach {
                val goalHistory = GoalHistory(
                    journeyId = it.uid,
                    progress = it.goal.progress,
                    goalValue = it.goal.value,
                    resetTime = Date()
                )
                goalHistoryDao.insert(goalHistory)
                Timber.d("Inserted goal history for journey ${it.name}.")
            }

            val rowsAffected = journeyDao.resetAllGoalProgressForFrequency(frequency)
            Timber.d("$rowsAffected goals of frequency ${frequency.name} reset.")

            Result.success()
        } catch (e: Exception) {
            Timber.e("Failed to reset goals of frequency ${frequency.name} progress.")

            Result.failure()
        }
    }
}