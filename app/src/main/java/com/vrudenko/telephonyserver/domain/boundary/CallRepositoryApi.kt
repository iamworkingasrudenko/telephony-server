package com.vrudenko.telephonyserver.domain.boundary

import com.vrudenko.telephonyserver.domain.model.Call
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface CallRepositoryApi {

    fun loadCallsAsync(): Single<List<Call>>

    fun saveCall(call: Call): Completable

    fun updateCall(call: Call): Completable

    fun loadLatestCallAsync(): Maybe<Call>

    fun loadLatestCall(): Call?

    fun loadCalls(): List<Call>

    fun subscribeSavedCallsUpdateEvents(): Flowable<Unit>

}
