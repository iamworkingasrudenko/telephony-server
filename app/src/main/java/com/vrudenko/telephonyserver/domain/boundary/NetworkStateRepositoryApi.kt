package com.vrudenko.telephonyserver.domain.boundary

import com.vrudenko.telephonyserver.domain.model.ConnectionInfo
import io.reactivex.rxjava3.core.Flowable

interface NetworkStateRepositoryApi {

    fun subscribeNetworkState(): Flowable<ConnectionInfo>

}