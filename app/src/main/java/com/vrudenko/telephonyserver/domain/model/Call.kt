package com.vrudenko.telephonyserver.domain.model

import java.util.*

data class Call(
    val id: Int? = null,
    val dateStarted: Date,
    val dateEnded: Date? = null,
    val phoneNumber: String?,
)