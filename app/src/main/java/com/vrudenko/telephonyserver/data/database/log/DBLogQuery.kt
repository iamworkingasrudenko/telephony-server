package com.vrudenko.telephonyserver.data.database.log

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vrudenko.telephonyserver.data.database.log.DBLogQuery.Companion.TABLE
import java.util.*

@Entity(tableName = TABLE)
class DBLogQuery(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Column.ID)
    var id: Int? = null,
    @ColumnInfo(name = Column.CALL_ID)
    val callId: Int,
    @ColumnInfo(name = Column.QUERY_DATE)
    val queryDate: Date
) {

    companion object {
        const val TABLE = "log_queries"

        object Column {
            const val ID = "log_id"
            const val CALL_ID = "queried_call_id"
            const val QUERY_DATE = "query_date"
        }
    }

}