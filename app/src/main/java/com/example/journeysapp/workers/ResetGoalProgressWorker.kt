package com.example.journeysapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.journeysapp.data.dao.JourneyDao
import com.example.journeysapp.data.model.GoalFrequency
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class ResetGoalProgressWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val journeyDao: JourneyDao,
) : CoroutineWorker(appContext, params) {
    companion object {
        const val KEY_FREQUENCY = "frequency"
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
            val rowsAffected = journeyDao.resetAllGoalProgressForFrequency(frequency)
            Timber.d("$rowsAffected goals of frequency ${frequency.name} reset.")

            Result.success()
        } catch (e: Exception) {
            Timber.e("Failed to reset goals of frequency ${frequency.name} progress.")

            Result.failure()
        }
    }
}