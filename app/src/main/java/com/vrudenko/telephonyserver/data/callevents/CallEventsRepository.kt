package com.vrudenko.telephonyserver.data.callevents

import android.os.Build
import com.vrudenko.telephonyserver.data.callevents.delegate.CallEventsFlowDelegate
import com.vrudenko.telephonyserver.data.callevents.delegate.CallEventsFlowDelegateImpl
import com.vrudenko.telephonyserver.data.callevents.delegate.CallEventsFlowDelegateImplR
import com.vrudenko.telephonyserver.data.datasource.CallScreeningDataSource
import com.vrudenko.telephonyserver.data.datasource.TelephonyStateDataSource
import com.vrudenko.telephonyserver.domain.model.CallEvent
import com.vrudenko.telephonyserver.domain.boundary.CallEventsRepositoryApi
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class CallEventsRepository @Inject constructor(
    telephonyStateDataSource: TelephonyStateDataSource,
    callScreeningDataSource: CallScreeningDataSource
) : CallEventsRepositoryApi {

    private val callEventsFlowDelegate: CallEventsFlowDelegate =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            CallEventsFlowDelegateImplR(telephonyStateDataSource, callScreeningDataSource)
        } else {
            CallEventsFlowDelegateImpl(telephonyStateDataSource)
        }

    override fun observeCallEvents(): Flowable<CallEvent> = callEventsFlowDelegate.observeCallEvents()

}
