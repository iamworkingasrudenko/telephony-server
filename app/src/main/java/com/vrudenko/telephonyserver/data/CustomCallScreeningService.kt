package com.vrudenko.telephonyserver.data

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import androidx.annotation.RequiresApi
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.data.datasource.CallScreeningDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * This service will be used
 *
 */
@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class CustomCallScreeningService : CallScreeningService() {

    @Inject
    lateinit var callScreeningDataSource: CallScreeningDataSource
    private val log by lazyLogger()

    override fun onScreenCall(callDetails: Call.Details) {
        log.debug("onScreenCall, callDetails: {}, callDetails.handle: {}, call direction: {}", callDetails, callDetails.handle, callDetails.callDirection)
        callScreeningDataSource.postCallScreeningDetails(callDetails)
        if (callDetails.callDirection == Call.Details.DIRECTION_INCOMING) {
            respondToCall(callDetails, CallResponse.Builder().build())
        }
    }

}