package com.kuba.journeysapp

import android.app.Application
import android.icu.util.Calendar
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kuba.journeysapp.data.model.GoalFrequency
import com.kuba.journeysapp.util.MILLISECONDS_IN_DAY
import com.kuba.journeysapp.workers.ResetGoalProgressWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        scheduleGoalResetWorker(GoalFrequency.DAILY)
        scheduleGoalResetWorker(GoalFrequency.WEEKLY)
        scheduleGoalResetWorker(GoalFrequency.MONTHLY)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.INFO)
            .build()

    private fun scheduleGoalResetWorker(frequency: GoalFrequency) {
        val repeatInterval: Long = when (frequency) {
            GoalFrequency.DAILY -> 1
            GoalFrequency.WEEKLY -> 7
            GoalFrequency.MONTHLY -> 30 // TODO This is not accurate
        }

        val inputData = Data.Builder().apply {
            putString(ResetGoalProgressWorker.KEY_FREQUENCY, frequency.name)
        }.build()

        val initialDelay = repeatInterval - 1 + calculateDelayUntilMidnight()

        val resetRequest = PeriodicWorkRequestBuilder<ResetGoalProgressWorker>(
            repeatInterval = repeatInterval,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            uniqueWorkName = ResetGoalProgressWorker.getUniqueWorkName(frequency),
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
            request = resetRequest
        )

        Timber.d(
            "Scheduled goal reset worker for ${frequency.name} set up, unique work name is ${
                ResetGoalProgressWorker.getUniqueWorkName(frequency)
            }"
        )
    }

    private fun calculateDelayUntilMidnight(): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis - System.currentTimeMillis()
    }

    private fun daysToMilliseconds(days: Long) = days * MILLISECONDS_IN_DAY
}