package com.codingstudio.mutualtransfer.ui.message.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.model.message.ModelMessageTransactions


class AdapterForMessageTransactions(private val userId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2


    inner class ViewHolderForSender(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val text_message_body = itemView.findViewById<TextView>(R.id.text_message_body)
        private val text_message_time = itemView.findViewById<TextView>(R.id.text_message_time)

        fun bind(modelMessageTransactions: ModelMessageTransactions) {

            text_message_body.text = modelMessageTransactions.message_content


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                modelMessageTransactions.sent_at?.let {
                    text_message_time.text = getTimeAgo(modelMessageTransactions.sent_at)
                }
            }
            else{
                modelMessageTransactions.sent_at?.let {
                    text_message_time.text = getTimeAgo2(modelMessageTransactions.sent_at)
                }
            }

        }

    }

    inner class ViewHolderForReceiver(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val text_message_body = itemView.findViewById<TextView>(R.id.text_message_body)
        private val text_message_time = itemView.findViewById<TextView>(R.id.text_message_time)

        fun bind(modelMessageTransactions: ModelMessageTransactions) {

            text_message_body.text = modelMessageTransactions.message_content


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                modelMessageTransactions.sent_at?.let {
                    text_message_time.text = getTimeAgo(modelMessageTransactions.sent_at)
                }
            }
            else{
                modelMessageTransactions.sent_at?.let {
                    text_message_time.text = getTimeAgo2(modelMessageTransactions.sent_at)
                }
            }

        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<ModelMessageTransactions>() {
        override fun areItemsTheSame(
            oldItem: ModelMessageTransactions,
            newItem: ModelMessageTransactions
        ): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ModelMessageTransactions,
            newItem: ModelMessageTransactions
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_SENDER) {
            ViewHolderForSender(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_bubble_sender, parent, false)
            )
        } else {
            ViewHolderForReceiver(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_bubble_receiver, parent, false)
            )
        }

    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {

        val sender_id = differ.currentList[position].sender_id ?: ""

        return if (sender_id == userId){
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVER
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val modelMessageTransactions = differ.currentList[position]

        if (holder is ViewHolderForSender) {
            holder.bind(modelMessageTransactions)
        } else if (holder is ViewHolderForReceiver) {
            holder.bind(modelMessageTransactions)
        }

    }


}