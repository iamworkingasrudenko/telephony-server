package com.vrudenko.telephonyserver.domain

import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.data.callevents.CallEventsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallManager @Inject constructor(
    callEventsRepository: CallEventsRepository
) {

    private val log by lazyLogger()

    init {
        callEventsRepository.observeCallEvents()
            .subscribe(
                {
                    log.debug("registered event {}", it)
                },
                {
                    log.error("unexpected error", it)
                }
            )
    }

}