package com.vrudenko.telephonyserver.domain.boundary

import com.vrudenko.telephonyserver.domain.model.CallWithContactName

interface ServerDataRepositoryApi {

    fun findOngoingCall(): CallWithContactName?

}