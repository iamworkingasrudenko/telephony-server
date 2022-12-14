package com.vrudenko.telephonyserver.data.call

import com.vrudenko.telephonyserver.common.schedulers.SchedulersProvider
import com.vrudenko.telephonyserver.data.call.datasource.CallTableUpdatesDataSource
import com.vrudenko.telephonyserver.data.database.AppDatabase
import com.vrudenko.telephonyserver.data.database.call.DBCall
import com.vrudenko.telephonyserver.domain.model.Call
import com.vrudenko.telephonyserver.domain.boundary.CallRepositoryApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CallRepository @Inject constructor(
    database: AppDatabase,
    private val callTableUpdatesDataSource: CallTableUpdatesDataSource,
    private val schedulers: SchedulersProvider
) : CallRepositoryApi {

    private val callDao = database.callDao()
    private val callMapper by lazy {
        CallMapper()
    }

    override fun loadCallsAsync(): Single<List<Call>> {
        return callDao.loadCallsAsync()
            .map { dbCalls ->
                dbCalls.map { callMapper.convertDbCallToCall(it) }
            }
            .subscribeOn(schedulers.io)
    }

    override fun loadLatestCallAsync(): Maybe<Call> {
        return callDao.loadLatestCallAsync()
            .map { callMapper.convertDbCallToCall(it) }
            .subscribeOn(schedulers.io)
    }

    override fun loadLatestCall(): Call? {
        return callDao.loadLatestCall()?.let { callMapper.convertDbCallToCall(it) }
    }

    override fun saveCall(call: Call): Completable = Completable.fromAction {
        callDao.saveCall(callMapper.convertCallToDbCall(call))
    }
        .doOnComplete { callTableUpdatesDataSource.postCallTableUpdated() }
        .subscribeOn(schedulers.io)

    override fun updateCall(call: Call): Completable = Completable.fromAction {
        callDao.updateCall(callMapper.convertCallToDbCall(call))
    }
        .doOnComplete { callTableUpdatesDataSource.postCallTableUpdated() }
        .subscribeOn(schedulers.io)

    override fun loadCalls(): List<Call> {
        return callDao.loadCalls().map { callMapper.convertDbCallToCall(it) }
    }

    override fun subscribeSavedCallsUpdateEvents(): Flowable<Unit> {
        return callTableUpdatesDataSource.subscribeCallTableUpdates()
    }

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