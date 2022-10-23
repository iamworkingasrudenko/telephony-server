package com.vrudenko.telephonyserver.data

sealed class CallState

object Idle : CallState()

abstract class Active(
    val callerNumber: String?
) : CallState()

class Ringing(
    callerNumber: String?
) : Active(callerNumber)

class InCall(
    callerNumber: String?
) : Active(callerNumber)
