package com.vrudenko.telephonyserver.data.database.log

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LogQueryDao {

    @Insert
    fun insertLogQueries(logQueries: List<DBLogQuery>)

    @Query("SELECT * from log_queries WHERE queried_call_id=:callId")
    fun getCallQueries(callId: Int): List<DBLogQuery>

}
