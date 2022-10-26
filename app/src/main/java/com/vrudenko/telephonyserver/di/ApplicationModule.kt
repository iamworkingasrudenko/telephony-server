package com.vrudenko.telephonyserver.di

import android.os.Build
import com.vrudenko.telephonyserver.common.AndroidSchedulersProvider
import com.vrudenko.telephonyserver.common.SchedulersProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideSchedulerProvider(): SchedulersProvider = AndroidSchedulersProvider()

    @Provides
    fun provideAppConfiguration(): AppConfiguration = AppConfiguration(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)

}