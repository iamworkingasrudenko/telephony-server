package com.vrudenko.telephonyserver.data.network.server

enum class Service(
    val serviceName: String,
    val uri: String
) {
    ROOT("root", "/"),
    STATUS("status", "/status"),
    LOG("log", "/log"),
}