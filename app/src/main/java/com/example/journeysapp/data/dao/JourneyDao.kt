package com.example.journeysapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.journeysapp.data.model.Journey

@Dao
interface JourneyDao {
    @Delete
    fun delete(journey: Journey)

    @Query("SELECT * FROM journey")
    fun getAll(): List<Journey>

    @Insert
    fun insert(journey: Journey)

    @Update
    fun update(journey: Journey)

    @Query(
        "UPDATE journey " +
                "SET goal_progress = goal_progress + :amount " +
                "WHERE uid = :journeyId " +
                "AND goal_progress < goal_value"
    )
    fun incrementGoalProgress(journeyId: Int, amount: Int = 1)

    @Query("UPDATE journey SET goal_progress = 0 WHERE uid = :journeyId ")
    fun resetGoalProgress(journeyId: Int)
}