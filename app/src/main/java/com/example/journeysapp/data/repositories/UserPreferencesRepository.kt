package com.example.journeysapp.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.journeysapp.data.model.internal.SortMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {
    private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
        name = SHARED_PREFS_NAME
    )

    companion object {
        private const val SHARED_PREFS_NAME = "user_preferences"

        private val SORT_MODE_KEY = stringPreferencesKey("SORT_MODE")
    }

    suspend fun saveSortMode(sortMode: SortMode) {
        applicationContext.userPreferencesDataStore.edit { preferences ->
            preferences[SORT_MODE_KEY] = sortMode.name
        }
    }

    fun getSortModeFlow(): Flow<SortMode> = applicationContext.userPreferencesDataStore.data.map {
        SortMode.valueOf(it[SORT_MODE_KEY] ?: SortMode.ALPHABETICALLY_DESC.name)
    }
}