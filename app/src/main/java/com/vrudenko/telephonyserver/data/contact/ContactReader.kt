package com.vrudenko.telephonyserver.data.contact

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.telephony.PhoneNumberUtils
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class ContactReader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val log by lazyLogger()

    fun readContactNameByNumber(phoneNumber: String): String? {
        val uri = Uri.withAppendedPath(
            Phone.CONTENT_FILTER_URI,
            Uri.encode(PhoneNumberUtils.normalizeNumber(phoneNumber))
        )
        val columns = arrayOf(Phone.CONTACT_ID, Phone.DISPLAY_NAME, Phone.NORMALIZED_NUMBER)
        val cursor = context.contentResolver.query(uri, columns, null, null, null)
        val names = arrayListOf<String>()

        cursor?.let { safeCursor ->
            while (safeCursor.moveToNext()) {
                log.debug("columnNames {}", safeCursor.columnNames)
                val columnIndex = safeCursor.getColumnIndex(Phone.DISPLAY_NAME)
                columnIndex.takeIf { it >= 0 }?.let {
                    names.add(safeCursor.getString(columnIndex))
                }
            }
            safeCursor.close()
        }
        return names.firstOrNull()
    }

}