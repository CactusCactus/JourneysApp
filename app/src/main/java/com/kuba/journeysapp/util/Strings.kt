package com.kuba.journeysapp.util

import android.content.Context
import com.kuba.journeysapp.R
import com.kuba.journeysapp.data.model.Goal
import com.kuba.journeysapp.data.model.GoalType
import java.text.DecimalFormat


fun Goal.goalSummaryString(context: Context) =
    "${goalType.toString(context)} $value ${unit.lowercase()} " +
            goalFrequency.toString(context).lowercase()

fun Goal.completionString(context: Context): String {
    val decimalFormat = DecimalFormat("##.##%")

    return when (goalType) {
        GoalType.LESS_THAN -> {
            if (progress > value) {
                context.getString(R.string.goal_reset_completion)
            } else {
                decimalFormat.format(1f - (progress.toDouble() / value))
            }
        }

        GoalType.MORE_THAN -> {
            if (progress == value) {
                context.getString(R.string.goal_reset_completion)
            } else {
                decimalFormat.format(progress.toDouble() / value)
            }
        }
    }
}