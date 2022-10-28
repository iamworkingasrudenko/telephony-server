package com.vrudenko.telephonyserver.domain.boundary

import com.vrudenko.telephonyserver.domain.model.CallEvent
import io.reactivex.rxjava3.core.Flowable

interface CallEventsRepositoryApi {

    fun observeCallEvents(): Flowable<CallEvent>

}

