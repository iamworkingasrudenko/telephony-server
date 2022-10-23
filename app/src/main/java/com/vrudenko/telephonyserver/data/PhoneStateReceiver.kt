package com.vrudenko.telephonyserver.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.data.datasource.TelephonyStateDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhoneStateReceiver : BroadcastReceiver() {
    // todo check PackageManager.hasSystemFeature(FEATURE_TELEPHONY)
    @Inject
    lateinit var telephonyStateDataSource: TelephonyStateDataSource

    private val log by lazyLogger()

    /*
    * Extra key used with the ACTION_PHONE_STATE_CHANGED broadcast for a String
    * containing the incoming or outgoing phone number.
    * This extra is only populated for receivers of the ACTION_PHONE_STATE_CHANGED
    * broadcast which have been granted the Manifest.permission.READ_CALL_LOG
    * and Manifest.permission.READ_PHONE_STATE  permissions.
    *
    * For incoming calls, the phone number is only guaranteed to be populated
    * when the EXTRA_STATE  changes from EXTRA_STATE_IDLE to EXTRA_STATE_RINGING.
    * If the incoming caller is from an unknown number, the extra will be populated
    * with an empty string.
    *
    * For outgoing calls, the phone number is only guaranteed to be populated
    * when the EXTRA_STATE  changes from EXTRA_STATE_IDLE  to EXTRA_STATE_OFFHOOK
    */
    override fun onReceive(context: Context, intent: Intent) {
        log.debug("intent action = {}", intent.action)
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return
        log.debug("Phone Number = {}", intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            handlePhoneStateIntentS(context, intent)
        } else {
            handlePhoneStateIntent(context, intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun handlePhoneStateIntentS(context: Context, intent: Intent) {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.registerTelephonyCallback(
            context.mainExecutor,
            object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    log.debug("call state changed: {}", state)
                    telephonyStateDataSource.postCallState(state)
                }
            }
        )
    }

    @Suppress("DEPRECATION")
    fun handlePhoneStateIntent(context: Context, intent: Intent) {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(object : PhoneStateListener() {
            @Suppress("DeprecatedCallableAddReplaceWith")
            @Deprecated("Deprecated in Java")
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                // TODO here we already have a phone number
                telephonyStateDataSource.postCallState(state, phoneNumber)
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }
}