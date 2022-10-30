package com.vrudenko.telephonyserver.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.domain.model.ConnectionInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStateSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val log by lazyLogger()

    private val networkStateSubject: BehaviorSubject<ConnectionInfo> =
        BehaviorSubject.createDefault(ConnectionInfo(false))

    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            networkStateSubject.onNext(ConnectionInfo(true))
            log.debug("onAvailable {}", network)
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            networkStateSubject.onNext(ConnectionInfo(true))
            log.debug("onCapabilitiesChanged {}\n", network, networkCapabilities)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            networkStateSubject.onNext(ConnectionInfo(false))
            log.debug("onLost {}", network)
        }
    }

    fun initialize() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun subscribeNetworkState(): Flowable<ConnectionInfo> =
        networkStateSubject
            .toFlowable(BackpressureStrategy.LATEST)

}
