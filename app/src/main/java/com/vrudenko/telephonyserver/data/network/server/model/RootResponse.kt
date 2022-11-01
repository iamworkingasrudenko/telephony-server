package com.vrudenko.telephonyserver.data.network.server.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class RootResponse(
    @SerializedName("start")
    val start: Date,
    @SerializedName("services")
    val services: List<ServerServiceResponseItem>
)
