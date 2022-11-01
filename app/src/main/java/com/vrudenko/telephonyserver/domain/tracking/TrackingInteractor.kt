package com.vrudenko.telephonyserver.domain.tracking

import com.vrudenko.telephonyserver.domain.CallProcessingStateProvider
import com.vrudenko.telephonyserver.domain.boundary.ServiceController
import javax.inject.Inject

class TrackingInteractor @Inject constructor(
    private val callProcessor: CallProcessingStateProvider,
    private val serviceControllerImplementation: ServiceController
) {

    fun observeProcessingRunning() = callProcessor.observeProcessingRunning()

    fun startTrackingCall() {
        serviceControllerImplementation.startProcessing()
    }

    fun stopTrackingCalls() {
        serviceControllerImplementation.stopProcessing()
    }

}