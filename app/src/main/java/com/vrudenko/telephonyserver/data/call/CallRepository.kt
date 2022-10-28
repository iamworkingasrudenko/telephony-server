package com.vrudenko.telephonyserver.data.call

import com.vrudenko.telephonyserver.common.SchedulersProvider
import com.vrudenko.telephonyserver.data.database.AppDatabase
import com.vrudenko.telephonyserver.data.database.call.DBCall
import com.vrudenko.telephonyserver.domain.model.Call
import com.vrudenko.telephonyserver.domain.boundary.CallRepositoryApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CallRepository @Inject constructor(
    database: AppDatabase,
    private val schedulers: SchedulersProvider
) : CallRepositoryApi {

    private val callDao = database.callDao()
    private val callMapper by lazy {
        CallMapper()
    }

    override fun loadCalls(): Single<List<Call>> {
        return callDao.loadCalls()
            .map { dbCalls ->
                dbCalls.map { callMapper.convertDbCallToCall(it) }
            }
            .subscribeOn(schedulers.io)
    }

    override fun loadLatestCall(): Maybe<Call> {
        return callDao.loadLatestCall()
            .map { callMapper.convertDbCallToCall(it) }
            .subscribeOn(schedulers.io)
    }

    override fun saveCall(call: Call): Completable = Completable.fromAction {
        callDao.saveCall(callMapper.convertCallToDbCall(call))
    }.subscribeOn(schedulers.io)

    override fun updateCall(call: Call): Completable = Completable.fromAction {
        callDao.updateCall(callMapper.convertCallToDbCall(call))
    }.subscribeOn(schedulers.io)

    class CallMapper {

        fun convertCallToDbCall(call: Call): DBCall = DBCall(
            id = call.id,
            phoneNumber = call.phoneNumber,
            startDate = call.dateStarted,
            endDate = call.dateEnded
        )

        fun convertDbCallToCall(dbCall: DBCall): Call = Call(
            id = dbCall.id,
            phoneNumber = dbCall.phoneNumber ?: "",
            dateStarted = dbCall.startDate,
            dateEnded = dbCall.endDate
        )

    }
}