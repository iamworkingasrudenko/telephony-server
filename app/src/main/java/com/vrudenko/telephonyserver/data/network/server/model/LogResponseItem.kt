package com.vrudenko.telephonyserver.data.network.server.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class LogResponseItem(
    @SerializedName("beginning")
    val startDate: Date,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("number")
    val phoneNumber: String,
    @SerializedName("name")
    val contactName: String?,
    @SerializedName("timesQueried")
    val timesQueried: Int
)