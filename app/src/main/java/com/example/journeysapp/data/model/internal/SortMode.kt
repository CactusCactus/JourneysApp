package com.example.journeysapp.data.model.internal

import android.content.Context
import com.example.journeysapp.R
import com.example.journeysapp.data.model.Journey

enum class SortMode {
    ALPHABETICALLY_ASC,
    ALPHABETICALLY_DESC,
    BY_PROGRESS_ASC,
    BY_PROGRESS_DESC;

    fun toString(context: Context) = when (this) {
        ALPHABETICALLY_ASC -> context.getString(R.string.sort_alphabetically_asc)
        ALPHABETICALLY_DESC -> context.getString(R.string.sort_alphabetically_desc)
        BY_PROGRESS_ASC -> context.getString(R.string.sort_progress_asc)
        BY_PROGRESS_DESC -> context.getString(R.string.sort_progress_desc)
    }
}

fun Collection<Journey>.sortedBy(sortMode: SortMode) =
    when (sortMode) {
        SortMode.ALPHABETICALLY_ASC -> this.sortedBy { it.name }
        SortMode.ALPHABETICALLY_DESC -> this.sortedByDescending { it.name }
        SortMode.BY_PROGRESS_ASC -> this.sortedBy { it.goal.progress.toFloat() / it.goal.value }
        SortMode.BY_PROGRESS_DESC -> this.sortedByDescending {
            it.goal.progress.toFloat() / it.goal.value
        }
    }