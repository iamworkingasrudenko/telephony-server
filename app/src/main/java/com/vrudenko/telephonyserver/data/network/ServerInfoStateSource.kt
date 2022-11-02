package com.vrudenko.telephonyserver.data.network

import com.vrudenko.telephonyserver.domain.model.RunningServerInfo
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Datasource that publishes updates about running server.
 */
@Singleton
class ServerInfoStateSource @Inject constructor() {

    private val runningServerSubject: BehaviorSubject<RunningServerInfo> = BehaviorSubject.createDefault(
        RunningServerInfo(false)
    )

    val runningServerInfo: RunningServerInfo
        get() = runningServerSubject.value ?: RunningServerInfo(false)

    fun publishServerInfo(serverInfo: RunningServerInfo) {
        runningServerSubject.onNext(serverInfo)
    }

    fun subscribeRunningServerInfo(): Flowable<RunningServerInfo> =
        runningServerSubject.toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()

}