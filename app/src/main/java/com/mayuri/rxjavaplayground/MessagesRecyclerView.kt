package com.mayuri.rxjavaplayground

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mayuri.rxjavaplayground.databinding.LayoutMessageBinding

class MessagesRecyclerView(private val messageList: List<MessageModel>) :
    RecyclerView.Adapter<MessagesRecyclerView.PaymentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder {
        val itemBinding =
            LayoutMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PaymentHolder, position: Int) {
        val messageModel: MessageModel = messageList[position]
        holder.itemBinding.message.text = messageModel.message
        holder.itemBinding.root.setBackgroundColor(
            messageModel.color

        )

    }

    override fun getItemCount(): Int = messageList.size

    class PaymentHolder(val itemBinding: LayoutMessageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

    }
}