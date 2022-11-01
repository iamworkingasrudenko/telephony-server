package com.vrudenko.telephonyserver.data.network.server

import com.vrudenko.telephonyserver.domain.boundary.CallRepositoryApi
import com.vrudenko.telephonyserver.domain.boundary.ContactRepositoryApi
import com.vrudenko.telephonyserver.domain.boundary.ServerDataRepositoryApi
import com.vrudenko.telephonyserver.domain.model.CallWithContactName
import javax.inject.Inject

class ServerDataRepository @Inject constructor(
    private val callRepository: CallRepositoryApi,
    private val contactRepositoryApi: ContactRepositoryApi
) : ServerDataRepositoryApi {

    override fun findOngoingCall(): CallWithContactName? {
        val call = callRepository.loadLatestCall()
        return if (call?.isOngoing == true) {
            val contactName = call.phoneNumber?.let { contactRepositoryApi.readContactName(it).contactName }
            CallWithContactName(
                id = call.id,
                dateStarted = call.dateStarted,
                dateEnded = call.dateEnded,
                phoneNumber = call.phoneNumber,
                contactName = contactName
            )
        } else null
    }

}