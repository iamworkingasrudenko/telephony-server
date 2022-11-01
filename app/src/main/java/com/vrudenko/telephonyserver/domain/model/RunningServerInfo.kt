package com.vrudenko.telephonyserver.domain.model

import java.util.*

data class RunningServerInfo(
    val isRunning: Boolean,
    val started: Date?,
    val ipv4Address: String?,
    val port: String?
)
