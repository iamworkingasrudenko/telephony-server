package com.vrudenko.telephonyserver.data.service

import android.content.Context
import androidx.core.content.ContextCompat
import com.vrudenko.telephonyserver.domain.boundary.ServiceController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ServiceControllerImplementation @Inject constructor(
    @ApplicationContext private val context: Context
) : ServiceController {

    private val startCommand
        get() = CallTrackingService.getStartCommand(context)

    private val stopCommand
        get() = CallTrackingService.getStopCommand(context)

    override fun startProcessing() {
        ContextCompat.startForegroundService(context, startCommand)
    }

    override fun stopProcessing() {
        ContextCompat.startForegroundService(context, stopCommand)
    }
}