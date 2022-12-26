package com.vrudenko.telephonyserver.data.call

import com.vrudenko.telephonyserver.common.schedulers.SchedulersProvider
import com.vrudenko.telephonyserver.data.call.datasource.CallTableUpdatesDataSource
import com.vrudenko.telephonyserver.data.database.AppDatabase
import com.vrudenko.telephonyserver.domain.model.Call
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import java.util.*

class CallRepositoryTest {

    private lateinit var callRepository: CallRepository

    private val database: AppDatabase = mockk {
        every { callDao() } returns mockk(relaxed = true)
    }
    private val callTableUpdatesDataSource: CallTableUpdatesDataSource = mockk()

    @Before
    fun setUp() {
        callRepository = spyk(
            CallRepository(
                database = database,
                callTableUpdatesDataSource = callTableUpdatesDataSource,
                schedulers = object : SchedulersProvider {
                    override val main: Scheduler
                        get() = Schedulers.trampoline()
                    override val io: Scheduler
                        get() = Schedulers.trampoline()
                    override val computation: Scheduler
                        get() = Schedulers.trampoline()
                }
            )
        )
    }

    @Test
    fun testSaveCall_triggersUpdateSource() {
        callRepository.saveCall(
            Call(
                id = null,
                dateStarted = Date(1667405729712L),
                dateEnded = null,
                phoneNumber = "+5550404503"
            )
        ).test()
        verify { callTableUpdatesDataSource.postCallTableUpdated() }
    }

    @Test
    fun testUpdateCall_triggersUpdateSource() {
        callRepository.saveCall(
            Call(
                id = null,
                dateStarted = Date(1667405729712L),
                dateEnded = Date(1667405730952L),
                phoneNumber = "+5550404503"
            )
        ).test()
        verify { callTableUpdatesDataSource.postCallTableUpdated() }
    }

}