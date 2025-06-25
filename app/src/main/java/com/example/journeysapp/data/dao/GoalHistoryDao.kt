package com.example.journeysapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.journeysapp.data.model.GoalHistory

@Dao
interface GoalHistoryDao {
    @Insert
    fun insert(goalHistory: GoalHistory): Long

    @Query("SELECT * FROM goal_history WHERE journey_id = :journeyId")
    fun getHistoryForJourney(journeyId: Long): List<GoalHistory>
}