package com.vrudenko.telephonyserver.data.network.server

import com.sun.net.httpserver.HttpServer
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.data.network.ServerInfoStateSource
import com.vrudenko.telephonyserver.data.network.server.httphandler.CallTrackerHttpHandler
import com.vrudenko.telephonyserver.domain.CallProcessingStateProvider
import com.vrudenko.telephonyserver.domain.model.RunningServerInfo
import com.vrudenko.telephonyserver.domain.server.NetUtils
import java.io.IOException
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerManager @Inject constructor(
    private val callTrackerHttpHandler: CallTrackerHttpHandler,
    private val serverInfoPublisher: ServerInfoStateSource,
    callProcessor: CallProcessingStateProvider
) {

    private val log by lazyLogger()
    private var httpServer: HttpServer? = null

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
            address?.let { safeAddress ->
                httpServer = HttpServer.create(InetSocketAddress(safeAddress, PORT), 0).apply {
                    executor = Executors.newCachedThreadPool()
                    createContext("/", callTrackerHttpHandler)
                }.apply {
                    executor = Executors.newCachedThreadPool()
                    start()
                    log.debug("Server is running on $safeAddress:$PORT")
                    serverInfoPublisher.publishServerInfo(
                        RunningServerInfo(
                            true,
                            Date(),
                            ipv4Address = safeAddress,
                            port = PORT,
                        )
                    )
                }
            }
        } catch (e: IOException) { //IO exceptions are not fatal
            log.error("server error ", e)
        }
    }

    fun stopServer() {
        httpServer?.stop(0)
        serverInfoPublisher.publishServerInfo(RunningServerInfo(false))
    }

    companion object {
        const val PORT = 2040
    }

}