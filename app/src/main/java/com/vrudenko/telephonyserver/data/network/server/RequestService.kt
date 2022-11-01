package com.vrudenko.telephonyserver.data.network.server

import com.vrudenko.telephonyserver.data.network.ServerInfoPublisher
import com.vrudenko.telephonyserver.data.network.server.model.DefaultResponse
import com.vrudenko.telephonyserver.data.network.server.model.LogResponseItem
import com.vrudenko.telephonyserver.data.network.server.model.RootResponse
import com.vrudenko.telephonyserver.data.network.server.model.ServerServiceResponseItem
import com.vrudenko.telephonyserver.data.network.server.model.StatusResponse
import com.vrudenko.telephonyserver.domain.model.RunningServerInfo
import javax.inject.Inject


/**
 * Business logic connected to REST requests processing
 */
class RequestService @Inject constructor(
    private val serverInfoPublisher: ServerInfoPublisher,
    private val repository: ServerDataRepository
) : RequestServiceApi {

    override fun getRootResponse(): RootResponse {
        val info = serverInfoPublisher.runningServerInfo
        return RootResponse(
            start = info.started,
            services = listOf(
                Service.STATUS.toServiceResponseItem(info),
                Service.LOG.toServiceResponseItem(info)
            )
        )
    }

    override fun getDefaultResponse(): DefaultResponse {
        val info = serverInfoPublisher.runningServerInfo
        return DefaultResponse(
            message = "Unrecognized request uri",
            services = listOf(
                Service.STATUS.toServiceResponseItem(info),
                Service.LOG.toServiceResponseItem(info)
            )
        )
    }

    override fun getStatus(): StatusResponse {
        val callWithContactName = repository.findOngoingCall()
        return StatusResponse(
            ongoing = callWithContactName != null,
            phoneNumber = callWithContactName?.phoneNumber,
            contactName = callWithContactName?.contactName
        )
    }

    override fun getLog(): List<LogResponseItem> {
        TODO()
    }

    private fun Service.toServiceResponseItem(runningServerInfo: RunningServerInfo): ServerServiceResponseItem {
        return ServerServiceResponseItem(
            serviceName,
            SERVICE_URI_FORMAT.format(runningServerInfo.ipv4Address, runningServerInfo.port, uri)
        )
    }

    companion object {
        const val SERVICE_URI_FORMAT = "http://%1s:%d%2s"
    }

}
