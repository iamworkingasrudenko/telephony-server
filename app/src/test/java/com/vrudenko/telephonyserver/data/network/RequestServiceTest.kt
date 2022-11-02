package com.vrudenko.telephonyserver.data.network

import com.vrudenko.telephonyserver.data.network.server.RequestService
import com.vrudenko.telephonyserver.data.network.server.model.LogResponseItem
import com.vrudenko.telephonyserver.domain.boundary.ServerDataRepositoryApi
import com.vrudenko.telephonyserver.domain.model.CallWithContactName
import com.vrudenko.telephonyserver.domain.model.RunningServerInfo
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Before
import org.junit.Test
import java.util.*

class RequestServiceTest {

    private lateinit var requestService: RequestService

    private val startedDate = Date(1667405729712L)

    private val repository: ServerDataRepositoryApi = mockk()

    private val serverInfoStateSource: ServerInfoStateSource = mockk {
        every { runningServerInfo } returns RunningServerInfo(
            isRunning = true,
            started = startedDate,
            ipv4Address = SERVER_ADDRESS,
            port = SERVER_PORT
        )
    }

    @Before
    fun setUp() {
        requestService = spyk(
            RequestService(
                serverInfoStateSource = serverInfoStateSource,
                repository = repository
            ),
        )
    }

    @Test
    fun getRootResponse_startDate() {
        val response = requestService.getRootResponse()
        assert(response.start == startedDate)

        val services = response.services
        assert(services.find { it.name == "status" } != null)
    }

    @Test
    fun getRootResponse_services_status() {
        val services = requestService.getRootResponse().services
        val statusService = services.find { it.name == "status" }
        assert(statusService != null)
        assert(statusService?.uri == "http://192.168.50.4:3000/status")
    }

    @Test
    fun getRootResponse_services_log() {
        val services = requestService.getRootResponse().services
        val logService = services.find { it.name == "log" }
        assert(logService != null)
        assert(logService?.uri == "http://192.168.50.4:3000/log")
    }

    @Test
    fun getStatus_noOngoingCall() {
        every { repository.findOngoingCall() } returns null
        val statusResponse = requestService.getStatus()
        assert(!statusResponse.ongoing)
        assert(statusResponse.contactName == null)
        assert(statusResponse.phoneNumber == null)
    }

    @Test
    fun getStatus_ongoingCall() {
        every { repository.findOngoingCall() } returns CallWithContactName(
            id = 1,
            dateStarted = Date(1667405759712L),
            dateEnded = null,
            phoneNumber = "+5559867",
            contactName = "Jane"
        )
        val statusResponse = requestService.getStatus()
        assert(statusResponse.ongoing)
        assert(statusResponse.contactName == "Jane")
        assert(statusResponse.phoneNumber == "+5559867")
    }

    @Test
    fun getLog_noHistory() {
        every { repository.loadCallsLog() } returns listOf()
        val logResponse = requestService.getLog()
        assert(logResponse.isEmpty())
    }

    @Test
    fun getLog_containsHistory() {
        every { repository.loadCallsLog() } returns listOf(
            LogResponseItem(
                startDate = Date(1667405759712L),
                duration = 8,
                phoneNumber = "+5559867",
                contactName = "Jane",
                timesQueried = 1
            )
        )
        val logResponse = requestService.getLog()
        assert(logResponse.size == 1)
    }

    companion object {
        const val SERVER_ADDRESS = "192.168.50.4"
        const val SERVER_PORT = 3000
    }
}
