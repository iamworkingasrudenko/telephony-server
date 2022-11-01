package com.vrudenko.telephonyserver.di

import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .setDateFormat(GSON_DATE_FORMAT)
            .create()

    companion object {
        private const val GSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"
    }

}
