package com.vrudenko.telephonyserver.domain.tracking

import com.vrudenko.telephonyserver.domain.CallProcessor
import com.vrudenko.telephonyserver.domain.repository.ServiceController
import javax.inject.Inject

class TrackingInteractor @Inject constructor(
    private val callProcessor: CallProcessor,
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