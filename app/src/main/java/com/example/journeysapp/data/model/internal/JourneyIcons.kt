package com.example.journeysapp.data.model.internal

import androidx.annotation.DrawableRes
import com.example.journeysapp.R

enum class JourneyIcons(@DrawableRes val icon: Int) {
    SMILE(R.drawable.ic_smile_24),
    NO_SMOKING(R.drawable.ic_smoke_free_24),
    OUTDOORS(R.drawable.ic_camping_24),
    RUNNING(R.drawable.ic_run_24),
    WAKE_UP(R.drawable.ic_alarm_24)
}