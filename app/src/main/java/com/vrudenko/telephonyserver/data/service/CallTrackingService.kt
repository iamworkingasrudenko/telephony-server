package com.vrudenko.telephonyserver.data.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vrudenko.telephonyserver.R
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.data.NotificationHelper
import com.vrudenko.telephonyserver.domain.CallProcessor
import com.vrudenko.telephonyserver.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class CallTrackingService : Service() {

    @Inject
    lateinit var callProcessor: CallProcessor

    @Inject
    lateinit var notificationHelper: NotificationHelper
    protected val log by lazyLogger()

    private val pendingIntent: PendingIntent
        get() = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            log.warn("onStartCommand(id: $startId, intent: $intent)")
            when ((intent ?: return START_REDELIVER_INTENT).action) {
                START_PROCESSING -> {
                    showNotification()
                    callProcessor.startTrackingCalls()
                }
                STOP_PROCESSING -> {
                    hideNotification()
                    stopSelf()
                }
            }
        } catch (error: Throwable) {
            log.error("onStartCommand({}) failed", error.javaClass.simpleName, error)
        }
        return START_REDELIVER_INTENT
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onDestroy() {
        log.debug("onDestroy")
        hideNotification()
        callProcessor.stopTrackingCalls()
    }

    private fun showNotification() {
        val (id, notification) = notificationHelper.showServiceNotification(
            intent = pendingIntent,
            idArgs = arrayOf(NOTIFICATION_ID),
            channelName = CHANNEL_NAME,
            body = { drawNotification() }
        )
        startForeground(id, notification)
    }

    private fun hideNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        notificationHelper.hide(
            idArgs = arrayOf(NOTIFICATION_ID)
        )
    }

    private fun NotificationCompat.Builder.drawNotification() {
        setSmallIcon(R.drawable.ic_baseline_call_24)  // the status icon
        setContentTitle(getString(R.string.notification_title))
        setContentText(getString(R.string.notification_text))
        setStyle(NotificationCompat.BigTextStyle().also {
            it.setBigContentTitle(getString(R.string.app_name))
            it.bigText(getString(R.string.notification_text))
        })
    }

    companion object {
        const val NOTIFICATION_ID = 1211
        private const val START_PROCESSING = "start processing"
        private const val STOP_PROCESSING = "stop processing"
        private const val CHANNEL_NAME = "Call Tracking"

        @JvmStatic
        fun getStartCommand(context: Context): Intent = context.newServiceIntent(START_PROCESSING)

        @JvmStatic
        fun getStopCommand(context: Context): Intent = context.newServiceIntent(STOP_PROCESSING)

        private fun Context.newServiceIntent(action: String) = Intent(this, callTrackingServiceClass).apply {
            this.action = action
        }

        /** Two foreground services are necessary because for Android Q and higher, the stopWithTask attribute doesn't work as expected
         *  https://issuetracker.google.com/issues/155088385 */
        private val callTrackingServiceClass: Class<out CallTrackingService> =
            when (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                true -> CallTrackingService::class.java
                else -> CallTrackingServiceForPostAndroidQ::class.java
            }
    }
}