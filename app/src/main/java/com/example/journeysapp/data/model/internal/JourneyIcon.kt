package com.example.journeysapp.data.model.internal

import androidx.annotation.DrawableRes
import androidx.room.TypeConverter
import com.example.journeysapp.R

enum class JourneyIcon(@DrawableRes val iconId: Int) {
    SMILE(R.drawable.ic_smile_24),
    NO_SMOKING(R.drawable.ic_smoke_free_24),
    OUTDOORS(R.drawable.ic_camping_24),
    RUNNING(R.drawable.ic_run_24),
    WAKE_UP(R.drawable.ic_alarm_24)
}

class JourneyIconTypeConverter {
    @TypeConverter
    fun toJourneyIcon(value: String) = enumValueOf<JourneyIcon>(value)

    @TypeConverter
    fun fromJourneyIcon(value: JourneyIcon) = value.name
}