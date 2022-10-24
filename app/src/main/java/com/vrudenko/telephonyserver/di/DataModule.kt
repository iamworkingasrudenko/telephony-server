package com.vrudenko.telephonyserver.di

import com.vrudenko.telephonyserver.data.CallEventsRepository
import com.vrudenko.telephonyserver.data.service.ServiceControllerImplementation
import com.vrudenko.telephonyserver.domain.repository.CallEventsRepositoryApi
import com.vrudenko.telephonyserver.domain.repository.ServiceController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindCallEventsRepository(callEventsRepository: CallEventsRepository): CallEventsRepositoryApi

    @Binds
    @Singleton
    abstract fun bindServiceController(serviceController: ServiceControllerImplementation): ServiceController

}