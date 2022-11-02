package com.vrudenko.telephonyserver.presentation.common

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallback<T>(
        private val old: List<T>,
        private val new: List<T>
) : DiffUtil.Callback() {
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = old[oldItemPosition] == new[newItemPosition]
    override fun getOldListSize() = old.size
    override fun getNewListSize() = new.size
}
