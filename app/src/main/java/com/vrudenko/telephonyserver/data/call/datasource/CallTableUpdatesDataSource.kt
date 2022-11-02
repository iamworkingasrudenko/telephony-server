package com.vrudenko.telephonyserver.data.call.datasource

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallTableUpdatesDataSource @Inject constructor() {

    private val callTableUpdateEvents: PublishSubject<Unit> = PublishSubject.create()

    fun postCallTableUpdated() {
        callTableUpdateEvents.onNext(Unit)
    }

    fun subscribeCallTableUpdates(): Flowable<Unit> {
        return callTableUpdateEvents.toFlowable(BackpressureStrategy.BUFFER)
    }

}
