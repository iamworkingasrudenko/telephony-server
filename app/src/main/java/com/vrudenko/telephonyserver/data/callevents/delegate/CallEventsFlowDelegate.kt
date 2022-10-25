package com.vrudenko.telephonyserver.data.callevents.delegate

import com.vrudenko.telephonyserver.domain.model.CallEvent
import io.reactivex.rxjava3.core.Flowable

interface CallEventsFlowDelegate {

    fun observeCallEvents(): Flowable<CallEvent>

    companion object {
        const val STATE_INACTIVE = -1
    }
}
