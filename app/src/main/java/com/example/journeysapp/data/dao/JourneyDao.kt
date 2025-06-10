package com.example.journeysapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.journeysapp.data.model.Journey

@Dao
interface JourneyDao {
    @Query("SELECT * FROM journey")
    fun getAll(): List<Journey>

    @Insert
    fun insert(journey: Journey)

    @Delete
    fun delete(journey: Journey)
}