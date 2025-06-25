package com.example.journeysapp.util

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.journeysapp.data.model.GoalFrequency
import com.example.journeysapp.workers.ResetGoalProgressWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
fun WorkManager.registerUpdatesForSuccessfulWorks(
    scope: CoroutineScope,
    onSuccess: suspend () -> Unit
) {
    getWorkInfosForUniqueWorkFlow(
        ResetGoalProgressWorker.getUniqueWorkName(GoalFrequency.DAILY)
    ).flatMapLatest { workInfos ->
        val latestWorkInfo = workInfos.firstOrNull()

        if (latestWorkInfo?.state == WorkInfo.State.SUCCEEDED) {
            flowOf(Unit)
        } else {
            emptyFlow()
        }
    }.onEach {
        onSuccess()
    }.stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, Unit)
}