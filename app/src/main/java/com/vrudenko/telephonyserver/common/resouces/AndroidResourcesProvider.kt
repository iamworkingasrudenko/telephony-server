package com.vrudenko.telephonyserver.common.resouces

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidResourcesProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourcesProvider {

    override fun getString(@StringRes stringResId: Int): String {
        return context.getString(stringResId)
    }

    override fun getString(@StringRes stringResId: Int, vararg formatArgs: Any?): String {
        return context.getString(stringResId, *formatArgs)
    }

}
