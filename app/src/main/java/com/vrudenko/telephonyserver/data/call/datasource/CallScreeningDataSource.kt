package com.vrudenko.telephonyserver.data.call.datasource

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import android.telecom.Call.Details as CallDetails
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class transforming Telephony events into reactive streams data source
 */
@Singleton
class CallScreeningDataSource @Inject constructor() {

    private val callDetailsSubject: BehaviorSubject<CallDetails> = BehaviorSubject.create()

    fun postCallScreeningDetails(details: CallDetails) {
        callDetailsSubject.onNext(details)
    }

    fun subscribeCallScreeningDetails(): Flowable<CallDetails> {
        return callDetailsSubject.toFlowable(BackpressureStrategy.LATEST)
    }

}