package com.vrudenko.telephonyserver.common.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Any.lazyLogger(tag: String? = null): Lazy<Logger> = lazy {
    LoggerFactory.getLogger(tag ?: javaClass.simpleName)
}
