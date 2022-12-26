package com.vrudenko.telephonyserver.domain.call

import com.vrudenko.telephonyserver.common.schedulers.SchedulersProvider
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.domain.boundary.CallRepositoryApi
import com.vrudenko.telephonyserver.domain.boundary.ContactRepositoryApi
import com.vrudenko.telephonyserver.domain.model.Call
import com.vrudenko.telephonyserver.domain.model.CallWithContactName
import com.vrudenko.telephonyserver.domain.model.ContactNameWrapper
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallLogInteractor @Inject constructor(
    private val callRepository: CallRepositoryApi,
    private val contactRepository: ContactRepositoryApi,
    private val schedulersProvider: SchedulersProvider
) {

    private val callLogSubject: BehaviorSubject<List<CallWithContactName>> = BehaviorSubject.create()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val log by lazyLogger()

    init {
        compositeDisposable.add(
            loadCallLog()
                .subscribe(
                    { calls -> callLogSubject.onNext(calls) },
                    { log.error("error loading calls ", it) }
                )
        )
        subscribeCallLogUpdates()
    }

    fun subscribeCallLogWithContactNames(): Flowable<List<CallWithContactName>> =
        callLogSubject
            .toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()

    private fun subscribeCallLogUpdates() {
        compositeDisposable.add(callRepository.subscribeSavedCallsUpdateEvents()
            .switchMapSingle {
                loadCallLog()
            }
            .doOnNext { callLogSubject.onNext(it) }
            .subscribe({}, { log.error("error subscribeCallLogUpdates", it) })
        )
    }

    private fun loadCallLog(): Single<List<CallWithContactName>> {
        return callRepository.loadCallsAsync()
            .flattenAsFlowable { it }
            .flatMapSingle { call ->
                contactRepository.readContactNameAsync(call.phoneNumber ?: "")
                    .map { call to it }
            }
            .toList()
            .map { pairList ->
                pairList.map { createCallWithContactName(it.first, it.second) }
            }
            .subscribeOn(schedulersProvider.io)
    }

    private fun createCallWithContactName(call: Call, contactNameWrapper: ContactNameWrapper): CallWithContactName {
        return CallWithContactName(
            id = call.id,
            dateStarted = call.dateStarted,
            dateEnded = call.dateEnded,
            phoneNumber = call.phoneNumber,
            contactName = contactNameWrapper.contactName
        )
    }

}