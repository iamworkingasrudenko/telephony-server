package com.vrudenko.telephonyserver.presentation.common

import android.view.View

fun View.changeVisibility(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}
