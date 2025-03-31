package com.codingstudio.mutualtransfer.ui.message.adapter

import android.content.Context
import android.graphics.Typeface
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
import com.codingstudio.mutualtransfer.model.message.ModelMessage

class AdapterForMessageParent(private val userId: String, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderForMessageParent(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewMessageParentSenderName = itemView.findViewById<TextView>(R.id.textViewMessageParentSenderName)
        private val textViewMessageParentSentOn = itemView.findViewById<TextView>(R.id.textViewMessageParentSentOn)
        private val textViewMessageParentMessageContent = itemView.findViewById<TextView>(R.id.textViewMessageParentMessageContent)

        fun bind(modelMessage: ModelMessage) {

            if(modelMessage.sender_id == userId){
                textViewMessageParentSenderName.text = modelMessage.receiver_name
            }
            else {
                textViewMessageParentSenderName.text = modelMessage.sender_name
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                modelMessage.sent_at?.let {
                    textViewMessageParentSentOn.text = getTimeAgo(modelMessage.sent_at)
                }
            }
            else{
                modelMessage.sent_at?.let {
                    textViewMessageParentSentOn.text = getTimeAgo2(modelMessage.sent_at)
                }
            }

            textViewMessageParentMessageContent.text = modelMessage.last_message_content
            if (modelMessage.is_read){
                val typeface = ResourcesCompat.getFont(context, R.font.roboto)
                textViewMessageParentMessageContent.typeface = typeface
                textViewMessageParentSenderName.typeface = typeface
            }
            else {
                val typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
                textViewMessageParentMessageContent.typeface = typeface
                textViewMessageParentSenderName.typeface = typeface
            }


            itemView.setOnClickListener {
                onMessageClickedListener?.let {
                    it(modelMessage)
                }
            }

        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<ModelMessage>() {
        override fun areItemsTheSame(
            oldItem: ModelMessage,
            newItem: ModelMessage
        ): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ModelMessage,
            newItem: ModelMessage
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderForMessageParent(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_layout_for_message, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messages = differ.currentList[position]
        (holder as ViewHolderForMessageParent).bind(messages)
    }

    private var onMessageClickedListener : ((ModelMessage) -> Unit) ?= null
    fun setOnMessageClickedListener(listener: ((ModelMessage) -> Unit)) {
        onMessageClickedListener = listener
    }

}