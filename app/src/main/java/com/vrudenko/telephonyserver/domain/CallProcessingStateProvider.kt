package com.vrudenko.telephonyserver.domain

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallProcessingStateProvider @Inject constructor() {

    private val processingRunningSubject = BehaviorSubject.createDefault(false)

    fun observeProcessingRunning(): Flowable<Boolean> = processingRunningSubject
        .toFlowable(BackpressureStrategy.LATEST)
        .distinctUntilChanged()

    fun startTrackingCalls() {
        processingRunningSubject.onNext(true)
    }

    fun stopTrackingCalls() {
        processingRunningSubject.onNext(false)
    }

}