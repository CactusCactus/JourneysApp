package com.example.journeysapp.data.model.internal

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.journeysapp.R

enum class JourneyContextMenuOption(@StringRes val label: Int, @DrawableRes val icon: Int) {
    DELETE(R.string.delete, R.drawable.ic_delete_24)
}
