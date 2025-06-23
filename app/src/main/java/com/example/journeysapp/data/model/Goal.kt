package com.example.journeysapp.data.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.TypeConverter
import com.example.journeysapp.R

data class Goal(
    @ColumnInfo(name = "goal_type") val goalType: GoalType,
    @ColumnInfo(name = "goal_value") val value: Int,
    @ColumnInfo(name = "goal_frequency") val goalFrequency: GoalFrequency,
    @ColumnInfo(name = "goal_progress") val progress: Int = 0
) {
    fun isCompleted() = progress == value
}

enum class GoalFrequency {
    DAILY,
    WEEKLY,
    MONTHLY;

    fun toString(context: Context) = when (this) {
        DAILY -> context.getString(R.string.goal_frequency_daily)
        WEEKLY -> context.getString(R.string.goal_frequency_weekly)
        MONTHLY -> context.getString(R.string.goal_frequency_monthly)
    }
}

enum class GoalType {
    LESS_THAN,
    MORE_THAN;

    fun toString(context: Context) = when (this) {
        LESS_THAN -> context.getString(R.string.goal_type_less_than)
        MORE_THAN -> context.getString(R.string.goal_type_more_than)
    }

    fun toCompletionString(context: Context) = when (this) {
        LESS_THAN -> context.getString(R.string.goal_completion_less_than)
        MORE_THAN -> context.getString(R.string.goal_completion_more_than)
    }
}

class GoalFrequencyTypeConverter {
    @TypeConverter
    fun toGoalFrequency(value: String) = enumValueOf<GoalFrequency>(value)

    @TypeConverter
    fun fromGoalFrequency(value: GoalFrequency) = value.name
}

class GoalTypeConverter {
    @TypeConverter
    fun toGoalType(value: String) = enumValueOf<GoalType>(value)

    @TypeConverter
    fun fromGoalType(value: GoalType) = value.name
}
