package com.vrudenko.telephonyserver.data.network.server.httphandler

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.vrudenko.telephonyserver.data.network.server.RequestServiceApi
import com.vrudenko.telephonyserver.data.network.server.Service.LOG
import com.vrudenko.telephonyserver.data.network.server.Service.ROOT
import com.vrudenko.telephonyserver.data.network.server.Service.STATUS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestDelegate @Inject constructor(
    private val gson: Gson,
    private val requestService: RequestServiceApi
) {

    fun processGetRequest(exchange: HttpExchange): String {
        return when (exchange.requestURI.toString().lowercase()) {
            ROOT.uri -> requestService.getRootResponse().toJson()
            STATUS.uri -> requestService.getStatus().toJson()
            LOG.uri -> requestService.getLog().toJson()
            else -> requestService.getDefaultResponse().toJson()
        }
    }

    private fun Any.toJson(): String {
        return gson.toJson(this)
    }

}