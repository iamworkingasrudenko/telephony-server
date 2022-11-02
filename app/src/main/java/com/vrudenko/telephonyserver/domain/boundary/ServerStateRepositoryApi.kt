package com.vrudenko.telephonyserver.domain.boundary

import com.vrudenko.telephonyserver.domain.model.RunningServerInfo
import io.reactivex.rxjava3.core.Flowable

interface ServerStateRepositoryApi {

    fun subscribeRunningServerInfo(): Flowable<RunningServerInfo>

}
