package com.vrudenko.telephonyserver.data.contact

import com.vrudenko.telephonyserver.domain.model.ContactNameWrapper
import com.vrudenko.telephonyserver.domain.boundary.ContactRepositoryApi
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val reader: ContactReader
) : ContactRepositoryApi {

    override fun readContactNameAsync(phoneNumber: String): Single<ContactNameWrapper> = Single.fromCallable {
        readContactName(phoneNumber)
    }

    override fun readContactName(phoneNumber: String): ContactNameWrapper {
        val name = reader.readContactNameByNumber(phoneNumber)
        return ContactNameWrapper(name)
    }

}