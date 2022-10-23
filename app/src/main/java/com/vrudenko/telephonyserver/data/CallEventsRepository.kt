package com.vrudenko.telephonyserver.data

import com.vrudenko.telephonyserver.common.SchedulersProvider
import com.vrudenko.telephonyserver.data.datasource.TelephonyStateDataSource
import com.vrudenko.telephonyserver.domain.repository.CallEventsRepositoryApi
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class CallEventsRepository @Inject constructor(
    private val telephonyStateDataSource: TelephonyStateDataSource,
    private val schedulersProvider: SchedulersProvider
): CallEventsRepositoryApi {

    override fun observeCallStateChanges(): Flowable<CallState> {
        return telephonyStateDataSource.observeCallState()
            .subscribeOn(schedulersProvider.io)
            .distinctUntilChanged()
    }

}