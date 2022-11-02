package com.vrudenko.telephonyserver.presentation.server.model

data class CallLogItem(
    val callId: Int,
    val phoneNumber: String,
    val contactName: String,
    val duration: String
)