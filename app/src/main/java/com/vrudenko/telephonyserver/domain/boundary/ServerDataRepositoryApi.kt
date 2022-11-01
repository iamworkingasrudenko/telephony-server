package com.vrudenko.telephonyserver.domain.boundary

import com.vrudenko.telephonyserver.data.network.server.model.LogResponseItem
import com.vrudenko.telephonyserver.domain.model.CallWithContactName

interface ServerDataRepositoryApi {

    fun findOngoingCall(): CallWithContactName?

    fun loadCallsLog(): List<LogResponseItem>

}