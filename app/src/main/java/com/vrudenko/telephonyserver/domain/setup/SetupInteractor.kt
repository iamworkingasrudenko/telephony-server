package com.vrudenko.telephonyserver.domain.setup

import com.vrudenko.telephonyserver.di.AppConfiguration
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
class SetupInteractor @Inject constructor(
    appConfiguration: AppConfiguration
) {

    private var userPermissionState by Delegates.observable(UserPermissions(callScreeningRoleRequired = appConfiguration.screeningRoleRequired)) { _, _, newValue ->
        userPermissionsStateSubject.onNext(newValue)
    }

    private val userPermissionsStateSubject: BehaviorSubject<UserPermissions> =
        BehaviorSubject.createDefault(userPermissionState)

    fun handleScreeningRoleGiven(roleGiven: Boolean): Completable = Completable.fromAction {
        userPermissionState = userPermissionState.copy(callScreeningRoleGiven = roleGiven)
    }

    fun handlePermissionsGiven(permissionsGiven: Boolean): Completable = Completable.fromAction {
        userPermissionState = userPermissionState.copy(permissionsGiven = permissionsGiven)
    }

    fun subscribeUserPermissionState(): Flowable<UserPermissions> {
        return userPermissionsStateSubject
            .toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
    }

}