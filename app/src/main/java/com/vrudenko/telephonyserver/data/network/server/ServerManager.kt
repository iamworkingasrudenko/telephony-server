package com.vrudenko.telephonyserver.data.network.server

import com.sun.net.httpserver.HttpServer
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.data.network.server.httphandler.CallTrackerHttpHandler
import com.vrudenko.telephonyserver.domain.CallProcessingStateProvider
import com.vrudenko.telephonyserver.domain.server.NetUtils
import java.io.IOException
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerManager @Inject constructor(
    private val callTrackerHttpHandler: CallTrackerHttpHandler,
    callProcessor: CallProcessingStateProvider
) {

    private val log by lazyLogger()
    private var mHttpServer: HttpServer? = null

    init {
        callProcessor.observeProcessingRunning()
            .subscribe(
                { running ->
                    when (running) {
                        true -> startServer()
                        else -> stopServer()
                    }
                },
                { log.error("UNEXPECTED observeProcessingRunning error: ", it) }
            )
    }

    fun startServer() {
        try {
            val address = NetUtils.getLocalIPAddress()?.hostAddress
            mHttpServer = HttpServer.create(InetSocketAddress(address!!, PORT), 0).apply {
                executor = Executors.newCachedThreadPool()
                createContext("/", callTrackerHttpHandler)
            }.apply {
                executor = Executors.newCachedThreadPool()
                start()
                log.debug("Server is running on $address")
            }

        } catch (e: IOException) { //those exceptions are not fatal
            log.error("server error ", e)
        }
    }

    fun stopServer() {
        mHttpServer?.stop(0)
    }

    companion object {
        const val PORT = 2040
    }

}