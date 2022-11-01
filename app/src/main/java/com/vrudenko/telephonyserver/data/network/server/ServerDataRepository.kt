package com.vrudenko.telephonyserver.data.network.server

import com.vrudenko.telephonyserver.domain.boundary.CallRepositoryApi
import com.vrudenko.telephonyserver.domain.boundary.ServerDataRepositoryApi
import javax.inject.Inject

class ServerDataRepository @Inject constructor(
    callRepository: CallRepositoryApi
): ServerDataRepositoryApi {



}