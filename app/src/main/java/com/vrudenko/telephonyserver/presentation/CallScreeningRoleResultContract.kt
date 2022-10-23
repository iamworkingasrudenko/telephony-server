package com.vrudenko.telephonyserver.presentation

import android.app.Activity.RESULT_OK
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class CallScreeningRoleResultContract: ActivityResultContract<Void?, Boolean>() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun createIntent(context: Context, input: Void?): Intent {
        val roleManager = context.getSystemService(AppCompatActivity.ROLE_SERVICE) as RoleManager
        return roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return when (resultCode) {
            RESULT_OK -> true
            else -> false
        }
    }

}
