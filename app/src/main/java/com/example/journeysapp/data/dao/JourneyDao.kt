package com.example.journeysapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.journeysapp.data.model.GoalFrequency
import com.example.journeysapp.data.model.Journey

@Dao
interface JourneyDao {
    @Delete
    fun delete(journey: Journey)

    @Query("SELECT * FROM journey WHERE uid = :journeyId")
    fun get(journeyId: Long): Journey

    @Query("SELECT * FROM journey")
    fun getAll(): List<Journey>

    @Query("SELECT * FROM journey WHERE goal_frequency = :frequency")
    fun getAllWithFrequency(frequency: GoalFrequency): List<Journey>

    @Insert
    fun insert(journey: Journey): Long

    @Update
    fun update(journey: Journey)

    @Query(
        "UPDATE journey " +
                "SET goal_progress = goal_progress + :amount " +
                "WHERE uid = :journeyId " +
                "AND goal_progress < goal_value AND goal_progress + :amount >= 0"
    )
    fun incrementGoalProgress(journeyId: Long, amount: Int = 1): Int

    @Query("UPDATE journey SET goal_progress = 0 WHERE uid = :journeyId ")
    fun resetGoalProgress(journeyId: Long)

    @Query("UPDATE journey SET goal_progress = 0 WHERE goal_frequency = :frequency")
    fun resetAllGoalProgressForFrequency(frequency: GoalFrequency): Int


}