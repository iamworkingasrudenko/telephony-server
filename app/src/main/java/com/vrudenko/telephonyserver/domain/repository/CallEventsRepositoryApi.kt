package com.vrudenko.telephonyserver.domain.repository

import com.vrudenko.telephonyserver.data.CallState
import io.reactivex.rxjava3.core.Flowable

interface CallEventsRepositoryApi {

    fun observeCallStateChanges(): Flowable<CallState>

}

