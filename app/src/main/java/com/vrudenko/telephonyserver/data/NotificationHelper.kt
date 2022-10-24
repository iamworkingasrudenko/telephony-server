package com.vrudenko.telephonyserver.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun showServiceNotification(
        intent: PendingIntent,
        channelName: String,
        vararg idArgs: Any,
        body: NotificationCompat.Builder.() -> Unit
    ): Pair<Int, Notification> {
        val channelId = context.packageName.plus(channelName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_MIN
            )
        }
        val id = generateNotificationId(*idArgs)
        val notification = NotificationCompat.Builder(context, context.packageName.plus(channelName))
            .setWhen(System.currentTimeMillis())
            .setContentIntent(intent)
            .setOngoing(true)
            .apply(body)
            .build()

        notificationManager.notify(id, notification)
        return id to notification
    }

    fun hide(
        vararg idArgs: Any
    ) {
        val id = generateNotificationId(idArgs)
        notificationManager.cancel(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        id: String,
        name: String,
        priority: Int
    ) {
        val notificationChannel = NotificationChannel(id, name, priority).apply {
            enableLights(false)
            enableVibration(false)
            setSound(null, null)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun generateNotificationId(vararg notificationId: Any): Int {
        return Objects.hash(this, *notificationId)
    }

}