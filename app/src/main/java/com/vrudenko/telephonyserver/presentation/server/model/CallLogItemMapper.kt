package com.vrudenko.telephonyserver.presentation.server.model

import com.vrudenko.telephonyserver.R
import com.vrudenko.telephonyserver.common.resouces.ResourcesProvider
import com.vrudenko.telephonyserver.domain.model.CallWithContactName
import java.util.concurrent.TimeUnit

class CallLogItemMapper(
    resourcesProvider: ResourcesProvider
) {

    private val unknownText = resourcesProvider.getString(R.string.unknown)
    private val durationFormat = resourcesProvider.getString(R.string.duration_formatter)

    fun map(callWithContactName: CallWithContactName): CallLogItem {
        return CallLogItem(
            callId = callWithContactName.id ?: 0, // as they are from database, should not be actually null
            phoneNumber = callWithContactName.phoneNumber ?: unknownText,
            contactName = callWithContactName.contactName ?: unknownText,
            duration = String.format(durationFormat, callWithContactName.getDuration())
        )
    }

    private fun CallWithContactName.getDuration(): Long {
        return if (dateEnded != null) {
            TimeUnit.MILLISECONDS.toSeconds(dateEnded.time - dateStarted.time)
        } else {
            //else the call is in progress
            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - dateStarted.time)
        }
    }

}