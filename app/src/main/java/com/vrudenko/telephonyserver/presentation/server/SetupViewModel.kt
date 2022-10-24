package com.vrudenko.telephonyserver.presentation.server

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vrudenko.telephonyserver.R
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.domain.setup.SetupInteractor
import com.vrudenko.telephonyserver.domain.setup.UserPermissions
import com.vrudenko.telephonyserver.domain.tracking.TrackingInteractor
import com.vrudenko.telephonyserver.presentation.server.model.PermissionError
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val setupInteractor: SetupInteractor,
    private val trackingInteractor: TrackingInteractor
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

    private val log by lazyLogger()

    private val compositeDisposable = CompositeDisposable()

    init {
        subscribeUserPermissionsState()
        subscribeTrackingIsRunning()
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
            .subscribe(
                { /* no op */ },
                { log.error("Unexpected error", it) }
            )
    }

    // We could handle permissions separately, but for simplicity just check all together
    fun handlePermissionsGranted(granted: Boolean) {
        setupInteractor.handlePermissionsGiven(granted)
            .subscribe(
                { /* no op */ },
                { log.error("Unexpected error", it) }
            )
    }

    private fun subscribeTrackingIsRunning() {
        trackingInteractor.observeProcessingRunning()
            .subscribe(
                {
                    _trackingInProgress.value = it
                    _trackingButtonTextSource.value = when (it) {
                        true -> context.getString(R.string.button_stop_tracking)
                        else -> context.getString(R.string.button_start_tracking)
                    }
                },
                { log.error("error subscribeTrackingIsRunning", it) }
            )
    }

    private fun subscribeUserPermissionsState() {
        compositeDisposable.add(
            setupInteractor.subscribeUserPermissionState()
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
            else -> context.getString(R.string.missing_permissions)
        }
        _errorMessageSource.value = PermissionError(errorMessage, !userPermissions.isGiven)
        _permissionsRequestRequiredState.value = !userPermissions.permissionsGiven
        _screeningRoleRequestRequiredState.value =
            userPermissions.callScreeningRoleRequired && !userPermissions.callScreeningRoleGiven
    }

}