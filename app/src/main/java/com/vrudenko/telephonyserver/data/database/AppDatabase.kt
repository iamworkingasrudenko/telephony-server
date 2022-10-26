package com.vrudenko.telephonyserver.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vrudenko.telephonyserver.data.database.call.CallDao
import com.vrudenko.telephonyserver.data.database.call.DBCall

@Database(entities = [DBCall::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun callDao(): CallDao

}