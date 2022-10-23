package com.vrudenko.telephonyserver.data

import android.telephony.TelephonyManager.CALL_STATE_IDLE
import android.telephony.TelephonyManager.CALL_STATE_OFFHOOK
import android.telephony.TelephonyManager.CALL_STATE_RINGING
import com.vrudenko.telephonyserver.common.extensions.lazyLogger


class CallStateConverter {

    private val log by lazyLogger()

    fun convertCallState(state: Int, callerNumber: String?): CallState {
        return when (state) {
            CALL_STATE_IDLE -> Idle
            CALL_STATE_RINGING -> Ringing(callerNumber)
            CALL_STATE_OFFHOOK -> InCall(callerNumber)
            else -> Idle.also {
                log.warn("Unknown call state: {}", state)
            }
        }
    }

}