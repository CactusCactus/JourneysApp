package com.example.journeysapp.util

import android.content.Context
import com.example.journeysapp.data.model.Goal


fun Goal.goalSummaryString(context: Context) =
    "${goalType.toString(context)} $value ${unit.lowercase()} " +
            goalFrequency.toString(context).lowercase()