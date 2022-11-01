package com.vrudenko.telephonyserver.data.network.server

import com.vrudenko.telephonyserver.data.database.AppDatabase
import com.vrudenko.telephonyserver.data.database.log.DBLogQuery
import com.vrudenko.telephonyserver.data.network.server.model.LogResponseItem
import com.vrudenko.telephonyserver.domain.boundary.CallRepositoryApi
import com.vrudenko.telephonyserver.domain.boundary.ContactRepositoryApi
import com.vrudenko.telephonyserver.domain.boundary.ServerDataRepositoryApi
import com.vrudenko.telephonyserver.domain.model.Call
import com.vrudenko.telephonyserver.domain.model.CallWithContactName
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ServerDataRepository @Inject constructor(
    private val callRepository: CallRepositoryApi,
    private val contactRepositoryApi: ContactRepositoryApi,
    appDatabase: AppDatabase
) : ServerDataRepositoryApi {

    private val logQueryDao = appDatabase.logQueryDao()

    override fun findOngoingCall(): CallWithContactName? {
        val call = callRepository.loadLatestCall()
        return if (call?.isOngoing == true) {
            val contactName = call.phoneNumber?.let { contactRepositoryApi.readContactName(it).contactName }
            CallWithContactName(
                id = call.id,
                dateStarted = call.dateStarted,
                dateEnded = call.dateEnded,
                phoneNumber = call.phoneNumber,
                contactName = contactName
            )
        } else null
    }

    override fun loadCallsLog(): List<LogResponseItem> {
        val calls = callRepository.loadCalls()
        val responseList = calls.map {
            LogResponseItem(
                startDate = it.dateStarted,
                duration = it.getDuration().toInt(),
                phoneNumber = it.phoneNumber.toString(),
                contactName = contactRepositoryApi.readContactName(it.phoneNumber ?: "").contactName,
                timesQueried = it.id?.let { id -> readCallQueryCount(id) } ?: 0
            )
        }
        val callIds = calls.map { it.id }.filterNotNull()
        logQueries(callIds)
        return responseList
    }

    private fun Call.getDuration(): Long {
        return if (dateEnded != null) {
            TimeUnit.MILLISECONDS.toSeconds(dateEnded.time - dateStarted.time)
        } else {
            //else the call is in progress
            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - dateStarted.time)
        }
    }

    private fun readCallQueryCount(callId: Int): Int {
        return logQueryDao.getCallQueries(callId).count()
    }

    private fun logQueries(callIds: List<Int>) {
        val now = Date()
        val queries = callIds.map {
            DBLogQuery(
                callId = it,
                queryDate = now
            )
        }
        logQueryDao.insertLogQueries(queries)
    }

}