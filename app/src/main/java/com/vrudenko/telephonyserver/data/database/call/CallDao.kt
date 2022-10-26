package com.vrudenko.telephonyserver.data.database.call

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface CallDao {

    @Query("SELECT * FROM calls ORDER BY call_start_date DESC")
    fun loadCalls(): Single<List<DBCall>>

    @Query("SELECT * FROM calls ORDER BY call_start_date DESC LIMIT 1")
    fun loadLatestCall(): Maybe<DBCall>

    @Insert
    fun saveCall(dbCall: DBCall)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCall(dbCall: DBCall)

}