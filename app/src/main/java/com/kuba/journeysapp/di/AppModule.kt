package com.kuba.journeysapp.di

import android.content.Context
import com.kuba.journeysapp.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context) =
        AppDatabase.builder(appContext).build()

    @Provides
    @Singleton
    fun provideJourneyDao(appDatabase: AppDatabase) = appDatabase.journeyDao()

    @Provides
    @Singleton
    fun provideGoalDao(appDatabase: AppDatabase) = appDatabase.goalHistoryDao()
}