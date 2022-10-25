package com.vrudenko.telephonyserver.domain.repository

import com.vrudenko.telephonyserver.domain.model.CallEvent
import io.reactivex.rxjava3.core.Flowable

interface CallEventsRepositoryApi {

    fun observeCallEvents(): Flowable<CallEvent>

}

