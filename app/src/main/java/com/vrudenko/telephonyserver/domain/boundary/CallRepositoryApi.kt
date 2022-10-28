package com.vrudenko.telephonyserver.domain.boundary

import com.vrudenko.telephonyserver.domain.model.Call
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface CallRepositoryApi {

    fun loadCalls(): Single<List<Call>>

    fun saveCall(call: Call): Completable

    fun updateCall(call: Call): Completable

    fun loadLatestCall(): Maybe<Call>

}