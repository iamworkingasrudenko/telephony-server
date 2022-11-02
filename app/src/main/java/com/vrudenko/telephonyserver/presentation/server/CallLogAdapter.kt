package com.vrudenko.telephonyserver.presentation.server

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vrudenko.telephonyserver.R
import com.vrudenko.telephonyserver.databinding.ItemCallLogBinding
import com.vrudenko.telephonyserver.presentation.common.DiffUtilCallback
import com.vrudenko.telephonyserver.presentation.common.inflateView
import com.vrudenko.telephonyserver.presentation.server.model.CallLogItem
import kotlin.properties.Delegates

class CallLogAdapter : RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    var items: List<CallLogItem> by Delegates.observable(mutableListOf()) { _, old, new ->
        DiffUtil.calculateDiff(DiffUtilCallback(old, new)).dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        return CallLogViewHolder(parent.inflateView(R.layout.item_call_log))
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class CallLogViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemCallLogBinding.bind(view)

        fun bind(model: CallLogItem) = with(binding) {
            textDuration.text = model.duration
            textContactName.text = model.contactName
            textPhoneNumber.text = model.phoneNumber
        }
    }

}