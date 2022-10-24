package com.vrudenko.telephonyserver.data.service

import android.content.Intent
import kotlin.system.exitProcess

class CallTrackingServiceForPostAndroidQ: CallTrackingService() {

    /** To close when user swipe application from recents */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        log.debug("onTaskRemoved()")
        exitProcess(0) // It's necessary because for Android Q and higher, the stopWithTask attribute doesn't work as expected and doesn't close app process
    }

}