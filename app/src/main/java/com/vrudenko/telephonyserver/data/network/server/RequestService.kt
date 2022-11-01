package com.vrudenko.telephonyserver.data.network.server

import com.vrudenko.telephonyserver.data.network.server.model.DefaultResponse
import com.vrudenko.telephonyserver.data.network.server.model.LogResponseItem
import com.vrudenko.telephonyserver.data.network.server.model.RootResponse
import com.vrudenko.telephonyserver.data.network.server.model.StatusResponse
import javax.inject.Inject


/**
 * Business logic connected to REST requests processing
 */
class RequestService @Inject constructor(

): RequestServiceApi {

    override fun getRootResponse(): RootResponse {
        TODO("Not yet implemented")
    }

    override fun getDefaultResponse(): DefaultResponse {
        TODO("Not yet implemented")
    }

    override fun getStatus(): StatusResponse {
        TODO()
    }

    override fun getLog(): List<LogResponseItem> {
        TODO()
    }

}
