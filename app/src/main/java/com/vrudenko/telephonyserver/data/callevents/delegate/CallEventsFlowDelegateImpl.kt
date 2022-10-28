package com.vrudenko.telephonyserver.data.callevents.delegate

import android.telephony.TelephonyManager.CALL_STATE_IDLE
import android.telephony.TelephonyManager.CALL_STATE_OFFHOOK
import android.telephony.TelephonyManager.CALL_STATE_RINGING
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.data.callevents.delegate.CallEventsFlowDelegate.Companion.STATE_INACTIVE
import com.vrudenko.telephonyserver.data.call.datasource.CallState
import com.vrudenko.telephonyserver.data.call.datasource.TelephonyStateDataSource
import com.vrudenko.telephonyserver.domain.model.CallEvent
import com.vrudenko.telephonyserver.domain.model.Ended
import com.vrudenko.telephonyserver.domain.model.Started
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.atomic.AtomicInteger

class CallEventsFlowDelegateImpl(
    private val telephonyStateDataSource: TelephonyStateDataSource,
) : CallEventsFlowDelegate {

    private val log by lazyLogger()

    private val lastStateCode: AtomicInteger = AtomicInteger(STATE_INACTIVE)
    private val callEventsSubject: BehaviorSubject<CallEvent> = BehaviorSubject.create()

    override fun observeCallEvents(): Flowable<CallEvent> {
        return telephonyStateDataSource.observeCallState()
            .map { callState ->
                log.debug("callState.stateCode = ${callState.stateCode}, phoneNumber = ${callState.phoneNumber}")
                if (callState.stateCode != lastStateCode.get()) {
                    handleCallStateChange(callState)
                }
            }
            .flatMap { callEventsSubject.toFlowable(BackpressureStrategy.BUFFER) }
            .distinctUntilChanged()
    }

    private fun handleCallStateChange(callState: CallState) {
        when (callState.stateCode) {
            CALL_STATE_IDLE, CALL_STATE_RINGING -> handleCallStateChangeToIdleOrRinging(callState.phoneNumber)
            CALL_STATE_OFFHOOK -> handleCallStateChangeToOffHook(callState.phoneNumber)
        }
        lastStateCode.set(callState.stateCode)
    }

    private fun handleCallStateChangeToIdleOrRinging(phoneNumber: String?) {
        val lastCode = lastStateCode.get()
        if (lastCode == CALL_STATE_OFFHOOK) {
            callEventsSubject.onNext(Ended(System.currentTimeMillis(), phoneNumber))
        } else {
            // we don't count RINGING calls, do nothing
        }
    }

    private fun handleCallStateChangeToOffHook(phoneNumber: String?) {
        callEventsSubject.onNext(Started(System.currentTimeMillis(), phoneNumber))
    }

}