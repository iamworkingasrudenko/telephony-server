package com.vrudenko.telephonyserver.data.datasource

import com.vrudenko.telephonyserver.data.CallState
import com.vrudenko.telephonyserver.data.CallStateConverter
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class transforming Telephony events into reactive streams data source
 */
@Singleton
class TelephonyStateDataSource @Inject constructor() {

    private val callStateSubject: BehaviorSubject<CallState> = BehaviorSubject.create()

    private val converter by lazy {
        CallStateConverter()
    }

    fun postCallState(state: Int, phoneNumber: String? = null) {
        callStateSubject.onNext(converter.convertCallState(state, phoneNumber))
    }

    fun observeCallState(): Flowable<CallState> {
        return callStateSubject
            .toFlowable(BackpressureStrategy.BUFFER)
    }

}