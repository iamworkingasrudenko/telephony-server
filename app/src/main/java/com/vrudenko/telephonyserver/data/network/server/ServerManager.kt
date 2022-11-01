package com.vrudenko.telephonyserver.data.network.server

import com.sun.net.httpserver.HttpServer
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.data.network.server.httphandler.CallTrackerHttpHandler
import com.vrudenko.telephonyserver.domain.CallProcessingStateProvider
import com.vrudenko.telephonyserver.domain.model.RunningServerInfo
import com.vrudenko.telephonyserver.domain.server.NetUtils
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.io.IOException
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerManager @Inject constructor(
    private val callTrackerHttpHandler: CallTrackerHttpHandler,
    callProcessor: CallProcessingStateProvider
) {

    private val log by lazyLogger()
    private var httpServer: HttpServer? = null

    private val runningServerSubject: BehaviorSubject<RunningServerInfo> = BehaviorSubject.createDefault(
        RunningServerInfo(false)
    )

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
                    runningServerSubject.onNext(
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
        runningServerSubject.onNext(RunningServerInfo(false))
    }

    fun subscribeRunningServerInfo(): Flowable<RunningServerInfo> =
        runningServerSubject.toFlowable(BackpressureStrategy.LATEST)

    companion object {
        const val PORT = 2040
    }

}