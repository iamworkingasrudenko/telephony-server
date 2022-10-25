package com.vrudenko.telephonyserver.domain.model

sealed class CallEvent(
    val timeStamp: Long
)

class Started(
    timeStamp: Long,
    val phoneNumber: String?
) : CallEvent(timeStamp) {
    override fun toString(): String {
        return "Started, timestamp=$timeStamp, phoneNumber=$phoneNumber"
    }
}

class Ended(
    timeStamp: Long
) : CallEvent(timeStamp) {
    override fun toString(): String {
        return "Ended, timestamp=$timeStamp"
    }
}