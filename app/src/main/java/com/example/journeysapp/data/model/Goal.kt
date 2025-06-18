package com.example.journeysapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "goal_type") val goalType: GoalType,
    @ColumnInfo(name = "value") val value: Int,
    @ColumnInfo(name = "goal_frequency") val goalFrequency: GoalFrequency
)

enum class GoalFrequency {
    DAILY,
    WEEKLY,
    MONTHLY
}

enum class GoalType {
    LESS_THAN,
    MORE_THAN
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
