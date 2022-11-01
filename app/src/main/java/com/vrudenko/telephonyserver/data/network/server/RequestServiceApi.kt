package com.vrudenko.telephonyserver.data.network.server

import com.vrudenko.telephonyserver.data.network.server.model.DefaultResponse
import com.vrudenko.telephonyserver.data.network.server.model.LogResponseItem
import com.vrudenko.telephonyserver.data.network.server.model.RootResponse
import com.vrudenko.telephonyserver.data.network.server.model.StatusResponse

interface RequestServiceApi {

    fun getRootResponse(): RootResponse

    fun getDefaultResponse(): DefaultResponse

    fun getStatus(): StatusResponse

    fun getLog(): List<LogResponseItem>

}