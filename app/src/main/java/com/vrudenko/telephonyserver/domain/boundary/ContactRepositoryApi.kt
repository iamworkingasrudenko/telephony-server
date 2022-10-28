package com.vrudenko.telephonyserver.domain.boundary

import com.vrudenko.telephonyserver.domain.model.ContactNameWrapper
import io.reactivex.rxjava3.core.Single

interface ContactRepositoryApi {

    fun readContactName(phoneNumber: String): Single<ContactNameWrapper>

}