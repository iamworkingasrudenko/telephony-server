package com.vrudenko.telephonyserver.data.network

import com.vrudenko.telephonyserver.domain.boundary.NetworkStateRepositoryApi
import com.vrudenko.telephonyserver.domain.model.ConnectionInfo
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class NetworkStateRepository @Inject constructor(
    private val networkStateSource: NetworkStateSource
): NetworkStateRepositoryApi {

    override fun subscribeNetworkState(): Flowable<ConnectionInfo> {
        return networkStateSource.subscribeNetworkState()
    }

}