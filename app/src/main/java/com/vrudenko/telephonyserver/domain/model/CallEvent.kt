package com.vrudenko.telephonyserver.domain.model

sealed class CallEvent(
    val timeStamp: Long,
    val phoneNumber: String?
)

class Started(
    timeStamp: Long,
    phoneNumber: String?
) : CallEvent(timeStamp, phoneNumber) {
    override fun toString(): String {
        return "Started, timestamp=$timeStamp, phoneNumber=$phoneNumber"
    }
}

class Ended(
    timeStamp: Long,
    phoneNumber: String?
) : CallEvent(timeStamp, phoneNumber) {
    override fun toString(): String {
        return "Ended, timestamp=$timeStamp"
    }
}