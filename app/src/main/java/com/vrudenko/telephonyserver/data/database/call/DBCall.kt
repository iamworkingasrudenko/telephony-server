package com.vrudenko.telephonyserver.data.database.call

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = DBCall.TABLE)
class DBCall(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Column.ID)
    var id: Int? = null,
    @ColumnInfo(name = Column.PHONE_NUMBER)
    val phoneNumber: String?,
    @ColumnInfo(name = Column.START_DATE)
    val startDate: Date,
    @ColumnInfo(name = Column.END_DATE)
    val endDate: Date?
) {

    companion object {
        const val TABLE = "calls"

        object Column {
            const val ID = "call_id"
            const val PHONE_NUMBER = "call_phone_number"
            const val START_DATE = "call_start_date"
            const val END_DATE = "call_end_date"
        }
    }
}