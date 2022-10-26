package com.vrudenko.telephonyserver.di

import android.content.Context
import androidx.room.Room
import com.vrudenko.telephonyserver.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, DATABASE_NAME
    ).build()

    companion object {
        private const val DATABASE_NAME = "call_tracker_db"
    }

}