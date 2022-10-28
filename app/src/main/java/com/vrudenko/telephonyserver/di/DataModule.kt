@file:Suppress("unused")

package com.vrudenko.telephonyserver.di

import com.vrudenko.telephonyserver.data.call.CallRepository
import com.vrudenko.telephonyserver.data.callevents.CallEventsRepository
import com.vrudenko.telephonyserver.data.contact.ContactRepository
import com.vrudenko.telephonyserver.data.service.ServiceControllerImplementation
import com.vrudenko.telephonyserver.domain.boundary.CallEventsRepositoryApi
import com.vrudenko.telephonyserver.domain.boundary.CallRepositoryApi
import com.vrudenko.telephonyserver.domain.boundary.ContactRepositoryApi
import com.vrudenko.telephonyserver.domain.boundary.ServiceController
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
    abstract fun bindCallRepository(callRepository: CallRepository): CallRepositoryApi

    @Binds
    @Singleton
    abstract fun bindContactRepository(contactRepository: ContactRepository): ContactRepositoryApi

    @Binds
    @Singleton
    abstract fun bindServiceController(serviceController: ServiceControllerImplementation): ServiceController

}