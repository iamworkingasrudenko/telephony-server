package com.vrudenko.telephonyserver.domain.server

import com.vrudenko.telephonyserver.domain.model.RunningServerInfo
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunningServerInteractor @Inject constructor(

) {

    fun subscribeRunningServer(): Flowable<RunningServerInfo> {
        return Flowable.never()
    }

}