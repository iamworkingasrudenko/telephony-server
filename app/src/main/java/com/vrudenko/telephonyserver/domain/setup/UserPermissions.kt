package com.vrudenko.telephonyserver.domain.setup

data class UserPermissions(
    val permissionsGiven: Boolean = false,
    val callScreeningRoleRequired: Boolean = false,
    val callScreeningRoleGiven: Boolean = false
) {

    val isGiven
        get() = permissionsGiven &&
                (!callScreeningRoleRequired || (callScreeningRoleRequired && callScreeningRoleGiven))

}