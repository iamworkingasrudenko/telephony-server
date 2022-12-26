package com.vrudenko.telephonyserver.common.resouces

import androidx.annotation.StringRes

interface ResourcesProvider {

    fun getString(@StringRes stringResId: Int): String

    fun getString(stringResId: Int, vararg formatArgs: Any?): String

}
