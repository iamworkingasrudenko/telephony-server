package com.vrudenko.telephonyserver.domain.model

import java.util.*

/**
 * @param isRunning if server is running at the moment
 * @param started server started date time
 * @param ipv4Address ipv4Address
 * @param port port
 */
data class RunningServerInfo(
    val isRunning: Boolean,
    val started: Date? = null,
    val ipv4Address: String? = null,
    val port: Int? = null,
)
