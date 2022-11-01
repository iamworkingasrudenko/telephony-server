package com.vrudenko.telephonyserver.data.network.server.model

import com.google.gson.annotations.SerializedName

data class ServerServiceResponseItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("uri")
    val uri: String
)