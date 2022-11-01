package com.vrudenko.telephonyserver.data.network.server.model

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    @SerializedName("ongoing")
    val ongoing: Boolean,
    @SerializedName("number")
    val phoneNumber: String? = null,
    @SerializedName("contactName")
    val contactName: String? = null
)
