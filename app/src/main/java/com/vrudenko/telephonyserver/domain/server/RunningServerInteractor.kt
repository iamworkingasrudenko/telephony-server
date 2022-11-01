package com.vrudenko.telephonyserver.domain.server

import com.vrudenko.telephonyserver.data.network.ServerInfoPublisher
import com.vrudenko.telephonyserver.domain.model.RunningServerInfo
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunningServerInteractor @Inject constructor(
    private val publisher: ServerInfoPublisher
) {

    fun subscribeRunningServer(): Flowable<RunningServerInfo> {
        return publisher
            .subscribeRunningServerInfo()
    }

}
