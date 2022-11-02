package com.vrudenko.telephonyserver.data.network.server.httphandler

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallTrackerHttpHandler @Inject constructor(
    private val getHandlerDelegate: RequestDelegate
) : HttpHandler {

    private val log by lazyLogger()

    override fun handle(exchange: HttpExchange?) {
        if (exchange == null) {
            log.warn("can't handle null exchange")
            return
        }
        val requestMethod = exchange.requestMethod
        if (requestMethod.equals(METHOD_GET, ignoreCase = true)) {
            handleGetRequest(exchange)
        } else {
            sendResponse(exchange, "Unsupported operation: $requestMethod")
        }
        exchange.close()
    }

    private fun handleGetRequest(exchange: HttpExchange) {
        sendResponse(exchange, getHandlerDelegate.processGetRequest(exchange))
    }

    private fun sendResponse(httpExchange: HttpExchange, responseText: String) {
        httpExchange.sendResponseHeaders(200, responseText.length.toLong())
        val outputStream = httpExchange.responseBody
        outputStream.write(responseText.toByteArray())
        outputStream.flush()
        outputStream.close()
    }

    companion object {
        const val METHOD_GET = "GET"
    }
}