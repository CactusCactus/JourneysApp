package com.example.journeysapp.data.model.internal

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.journeysapp.R

enum class JourneyContextMenuOption(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @ColorRes val tint: Int = R.color.black
) {
    DELETE(R.string.delete, R.drawable.ic_delete_24, R.color.color_error)
}
