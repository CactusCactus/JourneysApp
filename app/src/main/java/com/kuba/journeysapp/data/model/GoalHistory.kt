package com.kuba.journeysapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(
    tableName = "goal_history", foreignKeys = [
        ForeignKey(
            entity = Journey::class,
            parentColumns = ["uid"],
            childColumns = ["journey_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GoalHistory(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo("journey_id") val journeyId: Long,
    @ColumnInfo("progress") val progress: Int,
    @ColumnInfo("goal_value") val goalValue: Int,
    @ColumnInfo("reset_time") val resetTime: Date
)

class DateConverter {
    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time

    @TypeConverter
    fun fromTimestamp(value: Long): Date = Date(value)
}