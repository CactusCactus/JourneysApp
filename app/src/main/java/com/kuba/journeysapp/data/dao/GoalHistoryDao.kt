package com.kuba.journeysapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kuba.journeysapp.data.model.GoalHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalHistoryDao {
    @Insert
    fun insert(goalHistory: GoalHistory): Long

    @Query("SELECT * FROM goal_history")
    fun getAll(): List<GoalHistory>

    @Query("SELECT * FROM goal_history WHERE journey_id = :journeyId")
    fun getHistoryForJourney(journeyId: Long): List<GoalHistory>

    @Query("SELECT * FROM goal_history WHERE journey_id = :journeyId")
    fun getHistoryForJourneyAsFlow(journeyId: Long): Flow<List<GoalHistory>>
}