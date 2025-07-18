package com.kuba.journeysapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kuba.journeysapp.data.model.internal.JourneyIcon

@Entity
data class Journey(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "icon") val icon: JourneyIcon,
    @Embedded val goal: Goal
)
