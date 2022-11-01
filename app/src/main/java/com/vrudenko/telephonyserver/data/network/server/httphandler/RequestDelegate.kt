package com.vrudenko.telephonyserver.data.network.server.httphandler

import com.sun.net.httpserver.HttpExchange
import com.vrudenko.telephonyserver.data.network.server.RequestServiceApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestDelegate @Inject constructor(
    private val requestService: RequestServiceApi
    // todo provide gson to DI
) {

    fun processGetRequest(exchange: HttpExchange): String {
        return when (exchange.requestURI.toString().lowercase()) {
            REQUEST_URI_ROOT -> "root"
            REQUEST_URI_STATUS -> "status"
            REQUEST_URI_LOG -> "log"
            else -> "something else"
        }
    }

    companion object {
        private const val REQUEST_URI_ROOT = "/"
        private const val REQUEST_URI_STATUS = "/status"
        private const val REQUEST_URI_LOG = "/log"
    }

}