package com.vrudenko.telephonyserver.domain

import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.domain.model.Call
import com.vrudenko.telephonyserver.domain.model.Ended
import com.vrudenko.telephonyserver.domain.model.Started
import com.vrudenko.telephonyserver.domain.repository.CallEventsRepositoryApi
import com.vrudenko.telephonyserver.domain.repository.CallRepositoryApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallManager @Inject constructor(
    callEventsRepository: CallEventsRepositoryApi,
    private val callRepository: CallRepositoryApi
) {

    private val log by lazyLogger()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        compositeDisposable.add(callEventsRepository.observeCallEvents()
            .doOnNext { log.debug("received event {}", it) }
            .flatMapCompletable { event ->
                when (event) {
                    is Ended -> getEndedEventCompletable(event)
                    is Started -> handleStartedEventCompletable(event)
                }
            }
            .subscribe(
                { /* no op */ }, // todo push call event update event
                {
                    log.error("unexpected error", it)
                }
            )
        )
    }

    private fun getEndedEventCompletable(event: Ended): Completable {
        return callRepository.loadLatestCall()
            .flatMapCompletable { call ->
                log.debug("call id = {}, event.timestamp = {}", call.id, event.timeStamp)
                val newCall = call.copy(dateEnded = Date(event.timeStamp))
                callRepository.updateCall(newCall)
            }.doOnComplete { log.debug("completed an existing call in DB") }
    }

    private fun handleStartedEventCompletable(event: Started): Completable {
        return callRepository.saveCall(
            Call(
                dateStarted = Date(event.timeStamp),
                phoneNumber = event.phoneNumber
            )
        ).doOnComplete { log.debug("saved a new call to DB") }
    }

}