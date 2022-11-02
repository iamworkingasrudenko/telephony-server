package com.vrudenko.telephonyserver.data.network.server

import com.vrudenko.telephonyserver.data.network.ServerInfoStateSource
import com.vrudenko.telephonyserver.domain.boundary.ServerStateRepositoryApi
import com.vrudenko.telephonyserver.domain.model.RunningServerInfo
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class ServerStateRepository @Inject constructor(
    private val source: ServerInfoStateSource
): ServerStateRepositoryApi {

    override fun subscribeRunningServerInfo(): Flowable<RunningServerInfo> {
        return source.subscribeRunningServerInfo()
    }

}
