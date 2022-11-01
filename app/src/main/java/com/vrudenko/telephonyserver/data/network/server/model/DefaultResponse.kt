package com.vrudenko.telephonyserver.data.network.server.model

import com.google.gson.annotations.SerializedName

data class DefaultResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("services")
    val services: List<ServerServiceResponseItem>
)
