package com.vrudenko.telephonyserver.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vrudenko.telephonyserver.data.database.call.CallDao
import com.vrudenko.telephonyserver.data.database.call.DBCall
import com.vrudenko.telephonyserver.data.database.log.DBLogQuery
import com.vrudenko.telephonyserver.data.database.log.LogQueryDao

@Database(
    entities = [
        DBCall::class,
        DBLogQuery::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun callDao(): CallDao

    abstract fun logQueryDao(): LogQueryDao
}