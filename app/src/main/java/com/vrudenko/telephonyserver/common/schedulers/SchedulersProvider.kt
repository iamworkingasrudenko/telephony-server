package com.vrudenko.telephonyserver.common.schedulers

import io.reactivex.rxjava3.core.Scheduler

/**
 * Schedulers provider interface allows provide real implementation in code
 * and test implementations in tests.
 */
interface SchedulersProvider {

    val main: Scheduler

    val io: Scheduler

    val computation: Scheduler

}