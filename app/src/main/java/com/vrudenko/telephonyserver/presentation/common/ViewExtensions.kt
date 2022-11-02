package com.vrudenko.telephonyserver.presentation.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun View.changeVisibility(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun ViewGroup.inflateView(layoutId: Int): View = LayoutInflater.from(this.context)
    .inflate(layoutId, this, false)
