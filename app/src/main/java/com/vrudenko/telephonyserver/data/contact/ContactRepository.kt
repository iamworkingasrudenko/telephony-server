package com.vrudenko.telephonyserver.data.contact

import com.vrudenko.telephonyserver.domain.model.ContactNameWrapper
import com.vrudenko.telephonyserver.domain.boundary.ContactRepositoryApi
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val reader: ContactReader
) : ContactRepositoryApi {

    override fun readContactName(phoneNumber: String): Single<ContactNameWrapper> = Single.fromCallable {
        val name = reader.readContactNameByNumber(phoneNumber)
        return@fromCallable ContactNameWrapper(name)
    }

}