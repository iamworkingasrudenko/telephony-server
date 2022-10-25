package com.vrudenko.telephonyserver

import android.app.Application
import com.vrudenko.telephonyserver.domain.CallManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CallTrackerApplication: Application() {

    @Inject lateinit var callManager: CallManager

}