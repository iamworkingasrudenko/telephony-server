package com.vrudenko.telephonyserver.data.call.datasource

import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.domain.CallProcessor
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class transforming Telephony events into reactive streams data source
 */
@Singleton
class TelephonyStateDataSource @Inject constructor(
    callProcessor: CallProcessor,
    @ApplicationContext private val context: Context
) {

    private val telephonyManager
        get() = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private var telephonyCallback: TelephonyCallback? = null

    @Suppress("DEPRECATION")
    private var phoneStateListener: PhoneStateListener? = null

    init {
        callProcessor.observeProcessingRunning()
            .subscribe(
                { running ->
                    when (running) {
                        true -> subscribeTelephonyEvents()
                        else -> unsubscribeTelephonyEvents()
                    }
                },
                { log.error("UNEXPECTED observeProcessingRunning error: ", it) }
            )
    }

    private val log by lazyLogger()

    private val callStateSubject: BehaviorSubject<CallState> = BehaviorSubject.create()

    fun observeCallState(): Flowable<CallState> {
        return callStateSubject
            .toFlowable(BackpressureStrategy.BUFFER)
            .distinctUntilChanged()
    }

    private fun subscribeTelephonyEvents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            subscribePhoneStateS()// get events from phone states, but number from screening service
        } else {
            subscribePhoneState()  // get phone and events from here
        }
    }

    private fun unsubscribeTelephonyEvents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            unsubscribePhoneStateS()
        } else {
            unsubscribePhoneState()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun unsubscribePhoneStateS() {
        telephonyCallback?.let { telephonyManager.unregisterTelephonyCallback(it) }
    }

    @Suppress("DEPRECATION")
    private fun unsubscribePhoneState() {
        phoneStateListener?.let { telephonyManager.listen(it, PhoneStateListener.LISTEN_NONE) }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun subscribePhoneStateS() {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyCallback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
            override fun onCallStateChanged(state: Int) {
                log.debug("call state changed: {}", state)
                postCallState(state)
            }
        }.also {
            telephonyManager.registerTelephonyCallback(
                context.mainExecutor,
                it
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun subscribePhoneState() {
        phoneStateListener = object : PhoneStateListener() {
            @Suppress("DeprecatedCallableAddReplaceWith")
            @Deprecated("Deprecated in Java")
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                log.debug("call state changed: {} {}", state, phoneNumber)
                postCallState(state, phoneNumber)
            }
        }.also {
            telephonyManager.listen(it, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    private fun postCallState(state: Int, phoneNumber: String? = null) {
        callStateSubject.onNext(CallState(state, phoneNumber))
    }

}