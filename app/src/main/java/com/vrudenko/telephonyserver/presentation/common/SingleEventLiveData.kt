package com.vrudenko.telephonyserver.presentation.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import java.util.concurrent.atomic.AtomicBoolean

class SingleEventLiveData<T> : MutableLiveData<T>() {

    private val log by lazyLogger()

    private val isPending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            log.warn("Multiple observers registered but only one will be notified of changes")
        }

        super.observe(owner) {
            if (isPending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        }
    }

    override fun postValue(value: T) {
        isPending.set(true)
        super.postValue(value)
    }

}
