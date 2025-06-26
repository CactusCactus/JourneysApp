package com.kuba.journeysapp.workers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.kuba.journeysapp.App
import com.kuba.journeysapp.R
import com.kuba.journeysapp.data.dao.GoalHistoryDao
import com.kuba.journeysapp.data.dao.JourneyDao
import com.kuba.journeysapp.data.model.GoalFrequency
import com.kuba.journeysapp.data.model.GoalHistory
import com.kuba.journeysapp.data.model.Journey
import com.kuba.journeysapp.ui.main.MainActivity
import com.kuba.journeysapp.util.completionString
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.Date

@HiltWorker
class ResetGoalProgressWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
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

            // Nothing to reset
            if (journeysToReset.isEmpty() || journeysToReset.all { it.goal.progress == 0 }) {
                return Result.failure()
            }

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

            showGoalResetNotification(journeysToReset, frequency)

            val rowsAffected = journeyDao.resetAllGoalProgressForFrequency(frequency)
            Timber.d("$rowsAffected goals of frequency ${frequency.name} reset.")

            Result.success()
        } catch (e: Exception) {
            Timber.e("Failed to reset goals of frequency ${frequency.name} progress.")

            Result.failure()
        }
    }


    private fun showGoalResetNotification(journeysReset: List<Journey>, frequency: GoalFrequency) {
        val contentTitle: String = appContext.getString(
            R.string.goal_reset_notification_title,
            frequency.toString(appContext).lowercase()
        )

        val contentTextBuilder = StringBuilder()

        journeysReset.forEachIndexed { index, it ->
            if (it.goal.progress > 0) {
                contentTextBuilder.append("${it.name}: ${it.goal.completionString(appContext)}")

                if (index != journeysReset.lastIndex) {
                    contentTextBuilder.append("\n")
                }
            }
        }

        val contentText = contentTextBuilder.toString()

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent =
            PendingIntent.getActivity(appContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification =
            NotificationCompat.Builder(appContext, App.GOAL_RESET_WORKER_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Firebase.crashlytics.log("Notification permission not granted.")

            return
        }

        NotificationManagerCompat.from(appContext).notify(frequency.name.hashCode(), notification)
    }
}