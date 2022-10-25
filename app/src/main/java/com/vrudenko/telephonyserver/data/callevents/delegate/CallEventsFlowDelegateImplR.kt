package com.vrudenko.telephonyserver.data.callevents.delegate

import android.telecom.Call.Details
import android.telephony.TelephonyManager
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.data.callevents.delegate.CallEventsFlowDelegate.Companion.STATE_INACTIVE
import com.vrudenko.telephonyserver.data.datasource.CallScreeningDataSource
import com.vrudenko.telephonyserver.data.datasource.CallState
import com.vrudenko.telephonyserver.data.datasource.TelephonyStateDataSource
import com.vrudenko.telephonyserver.domain.model.CallEvent
import com.vrudenko.telephonyserver.domain.model.Ended
import com.vrudenko.telephonyserver.domain.model.Started
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.atomic.AtomicInteger

class CallEventsFlowDelegateImplR(
    private val telephonyStateDataSource: TelephonyStateDataSource,
    private val callScreeningDataSource: CallScreeningDataSource
) : CallEventsFlowDelegate {

    private val lastStateCode: AtomicInteger = AtomicInteger(STATE_INACTIVE)
    private val callEventsSubject: BehaviorSubject<CallEvent> = BehaviorSubject.create()

    private val log by lazyLogger()

    override fun observeCallEvents(): Flowable<CallEvent> {
        return telephonyStateDataSource.observeCallState()
            .withLatestFrom(callScreeningDataSource.subscribeCallScreeningDetails()) { callState, callDetails ->
                log.debug("callState.stateCode = ${callState.stateCode}, phoneNumber = ${callDetails.getPhoneNumber()}")
                if (callState.stateCode != lastStateCode.get()) {
                    handleCallStateChange(callState, callDetails.getPhoneNumber())
                }
            }
            .flatMap { callEventsSubject.toFlowable(BackpressureStrategy.BUFFER) }
            .distinctUntilChanged()
    }

    private fun handleCallStateChange(callState: CallState, phoneNumber: String) {
        log.debug("handleCallStateChange, callState.stateCode {}", callState.stateCode)
        when (callState.stateCode) {
            TelephonyManager.CALL_STATE_IDLE, TelephonyManager.CALL_STATE_RINGING -> handleCallStateChangeToIdleOrRinging()
            TelephonyManager.CALL_STATE_OFFHOOK -> handleCallStateChangeToOffHook(phoneNumber)
        }
        lastStateCode.set(callState.stateCode)
    }

    private fun handleCallStateChangeToIdleOrRinging() {
        val lastCode = lastStateCode.get()
        if (lastCode == TelephonyManager.CALL_STATE_OFFHOOK) {
            callEventsSubject.onNext(Ended(System.currentTimeMillis()))
        } else {
            // we don't count RINGING calls, do nothing
        }
    }

    private fun handleCallStateChangeToOffHook(phoneNumber: String) {
        callEventsSubject.onNext(Started(System.currentTimeMillis(), phoneNumber))
    }

    private fun Details.getPhoneNumber(): String {
        return handle.schemeSpecificPart
    }

}