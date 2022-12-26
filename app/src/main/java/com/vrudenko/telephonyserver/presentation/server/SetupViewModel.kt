package com.vrudenko.telephonyserver.presentation.server

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vrudenko.telephonyserver.R
import com.vrudenko.telephonyserver.common.schedulers.SchedulersProvider
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.common.resouces.ResourcesProvider
import com.vrudenko.telephonyserver.domain.call.CallLogInteractor
import com.vrudenko.telephonyserver.domain.connection.ConnectionInfoInteractor
import com.vrudenko.telephonyserver.domain.server.RunningServerInteractor
import com.vrudenko.telephonyserver.domain.setup.SetupInteractor
import com.vrudenko.telephonyserver.domain.setup.UserPermissions
import com.vrudenko.telephonyserver.domain.tracking.TrackingInteractor
import com.vrudenko.telephonyserver.presentation.server.model.CallLogItem
import com.vrudenko.telephonyserver.presentation.server.model.CallLogItemMapper
import com.vrudenko.telephonyserver.presentation.server.model.PermissionError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val setupInteractor: SetupInteractor,
    private val trackingInteractor: TrackingInteractor,
    private val connectionInfoInteractor: ConnectionInfoInteractor,
    private val runningServerInteractor: RunningServerInteractor,
    private val callLogInteractor: CallLogInteractor,
    private val schedulersProvider: SchedulersProvider
) : ViewModel() {

    private val _trackingInProgress: MutableLiveData<Boolean> = MutableLiveData()

    private val _trackingButtonTextSource: MutableLiveData<String> = MutableLiveData()
    val trackingButtonTextSource: LiveData<String>
        get() = _trackingButtonTextSource

    private val _errorMessageSource: MutableLiveData<PermissionError> = MutableLiveData()
    val errorMessageSource: LiveData<PermissionError>
        get() = _errorMessageSource

    private val _screeningRoleRequestRequiredState = MutableLiveData<Boolean>()
    val screeningRoleRequestRequiredState: LiveData<Boolean>
        get() = _screeningRoleRequestRequiredState

    private val _permissionsRequestRequiredState = MutableLiveData(false)
    val permissionsRequestRequiredState: LiveData<Boolean>
        get() = _permissionsRequestRequiredState

    private val _connectionTextState = MutableLiveData<String>()
    val connectionTextState: LiveData<String>
        get() = _connectionTextState

    private val _serverTextState = MutableLiveData<String>()
    val serverTextState: LiveData<String>
        get() = _serverTextState

    private val _buttonActiveState = MutableLiveData(true)
    val buttonActiveState: LiveData<Boolean>
        get() = _buttonActiveState

    private val _callLogItemsState = MutableLiveData<List<CallLogItem>>()
    val callLogItemsState: LiveData<List<CallLogItem>>
        get() = _callLogItemsState

    private val callLogItemMapper by lazy {
        CallLogItemMapper(resourcesProvider)
    }

    private val log by lazyLogger()

    private val compositeDisposable = CompositeDisposable()

    init {
        subscribeUserPermissionsState()
        subscribeTrackingIsRunning()
        subscribeConnectionState()
        subscribeRunningServerInfo()
        subscribeLogItems()
    }

    fun handleButtonCallsTrackingClick() {
        if (_trackingInProgress.value == true) {
            trackingInteractor.stopTrackingCalls()
        } else {
            trackingInteractor.startTrackingCall()
        }
    }

    fun handleScreeningRoleRequestResult(granted: Boolean) {
        setupInteractor.handleScreeningRoleGiven(granted)
            .observeOn(schedulersProvider.main)
            .subscribe(
                { /* no op */ },
                { log.error("Unexpected error", it) }
            )
    }

    // We could handle permissions separately, but for simplicity just check all together
    fun handlePermissionsGranted(granted: Boolean) {
        setupInteractor.handlePermissionsGiven(granted)
            .observeOn(schedulersProvider.main)
            .subscribe(
                { /* no op */ },
                { log.error("Unexpected error", it) }
            )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    // check onClear method
    private fun subscribeTrackingIsRunning() = compositeDisposable.add(
        trackingInteractor.observeProcessingRunning()
            .observeOn(schedulersProvider.main)
            .subscribe(
                {
                    _trackingInProgress.value = it
                    _trackingButtonTextSource.value = when (it) {
                        true -> resourcesProvider.getString(R.string.button_stop_tracking)
                        else -> resourcesProvider.getString(R.string.button_start_tracking)
                    }
                },
                { log.error("error subscribeTrackingIsRunning", it) }
            )
    )

    private fun subscribeConnectionState() = compositeDisposable.add(
        connectionInfoInteractor.subscribeConnectionProper()
            .observeOn(schedulersProvider.main)
            .subscribe(
                { connectionIsProper ->
                    val connectionText = when (connectionIsProper) {
                        true -> resourcesProvider.getString(R.string.connection_ok)
                        else -> resourcesProvider.getString(R.string.proper_connection_missing)
                    }
                    _connectionTextState.value = connectionText
                    _buttonActiveState.value = connectionIsProper
                },
                { log.error("error subscribeConnectionState", it) }
            )
    )

    private fun subscribeUserPermissionsState() {
        compositeDisposable.add(
            setupInteractor.subscribeUserPermissionState()
                .observeOn(schedulersProvider.main)
                .subscribe(
                    { state -> handleUserPermissionsState(state) },
                    { log.error("Unexpected error", it) }
                )
        )
    }

    private fun handleUserPermissionsState(userPermissions: UserPermissions) {
        log.debug("handleUserPermissionsState {}", userPermissions)
        val errorMessage = when (userPermissions.isGiven) {
            true -> ""
            else -> resourcesProvider.getString(R.string.missing_permissions)
        }
        _errorMessageSource.value = PermissionError(errorMessage, !userPermissions.isGiven)
        _permissionsRequestRequiredState.value = !userPermissions.permissionsGiven
        _screeningRoleRequestRequiredState.value =
            userPermissions.callScreeningRoleRequired && !userPermissions.callScreeningRoleGiven
    }

    private fun subscribeRunningServerInfo() = compositeDisposable.add(
        runningServerInteractor
            .subscribeRunningServer()
            .observeOn(schedulersProvider.main)
            .subscribe(
                {
                    _serverTextState.value = if (it.isRunning && it.ipv4Address != null && it.port != null) {
                        resourcesProvider.getString(R.string.running_server_format, it.ipv4Address, it.port)
                    } else {
                        resourcesProvider.getString(R.string.running_server_none)
                    }
                },
                { log.error("subscribeRunningServerInfo error", it) }
            )
    )

    private fun subscribeLogItems() = compositeDisposable.add(
        callLogInteractor.subscribeCallLogWithContactNames()
            .observeOn(schedulersProvider.main)
            .subscribe(
                { calls ->
                    _callLogItemsState.value = calls.map { callLogItemMapper.map(it) }
                },

                { log.error("subscribeLogItems error", it) }
            )
    )

}