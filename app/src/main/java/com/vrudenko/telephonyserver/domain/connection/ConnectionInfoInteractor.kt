package com.vrudenko.telephonyserver.domain.connection

import com.vrudenko.telephonyserver.domain.boundary.NetworkStateRepositoryApi
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionInfoInteractor @Inject constructor(
    private val connectionRepository: NetworkStateRepositoryApi
) {

    fun subscribeConnectionProper(): Flowable<Boolean> {
        return connectionRepository.subscribeNetworkState()
            .map { it.isWifi }
    }

}
