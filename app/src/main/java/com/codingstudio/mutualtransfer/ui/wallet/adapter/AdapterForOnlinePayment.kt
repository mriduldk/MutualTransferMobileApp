package com.codingstudio.mutualtransfer.ui.wallet.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.model.online_payment.OnlinePayment
import java.lang.Exception
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AdapterForOnlinePayment() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderForOnlinePayment(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewTransition = itemView.findViewById<View>(R.id.viewTransition)

        private val textViewOnlinePaymentOrderId = itemView.findViewById<TextView>(R.id.textViewOnlinePaymentOrderId)
        private val textViewOnlinePaymentAmount = itemView.findViewById<TextView>(R.id.textViewOnlinePaymentAmount)
        private val textViewOnlinePaymentCoins = itemView.findViewById<TextView>(R.id.textViewOnlinePaymentCoins)
        private val textViewOnlinePaymentPaymentMode = itemView.findViewById<TextView>(R.id.textViewOnlinePaymentPaymentMode)
        private val textViewOnlinePaymentReceipt = itemView.findViewById<TextView>(R.id.textViewOnlinePaymentReceipt)
        private val textViewOnlinePaymentStatus = itemView.findViewById<TextView>(R.id.textViewOnlinePaymentStatus)
        private val textViewOnlinePaymentDateTime = itemView.findViewById<TextView>(R.id.textViewOnlinePaymentDateTime)
        private val constraintLayoutRefreshPaymentStatusButton = itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutRefreshPaymentStatusButton)
        private val textViewContactSupportButton = itemView.findViewById<TextView>(R.id.textViewContactSupportButton)

        fun bind(onlinePayment: OnlinePayment) {

            textViewOnlinePaymentOrderId.text = "Order ID: ${onlinePayment.order_id}"
            textViewOnlinePaymentAmount.text = "Amount: ₹ ${convertToDecimal(onlinePayment.amount)}"
            textViewOnlinePaymentCoins.text = "Coins: ${onlinePayment.coins}"

            var paymentMethod = ""
            onlinePayment.payment_mode?.let {
                paymentMethod = it
            }
            textViewOnlinePaymentPaymentMode.text = "Payment Mode: $paymentMethod"
            textViewOnlinePaymentReceipt.text = "Receipt: ${onlinePayment.receipt}"
            textViewOnlinePaymentStatus.text = "Status: ${onlinePayment.status}"

            val paymentDate = onlinePayment.order_created_at?.let { formatDate(it) }

            textViewOnlinePaymentDateTime.text = "Payment Date: $paymentDate"

            if (onlinePayment.status == "paid") {
                constraintLayoutRefreshPaymentStatusButton.visibility = View.GONE
            }
            else {
                constraintLayoutRefreshPaymentStatusButton.visibility = View.VISIBLE
            }


            constraintLayoutRefreshPaymentStatusButton.setOnClickListener {
                onRefreshPaymentClickedListener?.let {
                    it(onlinePayment)
                }
            }

            textViewContactSupportButton.setOnClickListener {
                onContactSupportClickedListener?.let {
                    it(onlinePayment)
                }
            }

        }

    }

    private fun convertToDecimal(amount: String?): String {

        return if (amount != null) {
            try {
                val amountBigDecimal = BigDecimal(amount.trim())
                val result = amountBigDecimal.divide(BigDecimal(100))
                val decimalFormat = DecimalFormat("#,##0.00")
                decimalFormat.format(result)
            } catch (ex: Exception){
                ""
            }
        } else {
            ""
        }
    }

    private fun formatDate(dateString: String): String {

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())

        } catch (ex: Exception){
            ""
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<OnlinePayment>() {
        override fun areItemsTheSame(
            oldItem: OnlinePayment,
            newItem: OnlinePayment
        ): Boolean {
           return oldItem.online_payment_id == newItem.online_payment_id
        }

        override fun areContentsTheSame(
            oldItem: OnlinePayment,
            newItem: OnlinePayment
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderForOnlinePayment(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_layout_for_online_transaction, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val searchedHistory = differ.currentList[position]
        (holder as ViewHolderForOnlinePayment).bind(searchedHistory)
    }

    private var onRefreshPaymentClickedListener : ((OnlinePayment) -> Unit) ?= null
    fun setRefreshPaymentClickedListener(listener: ((OnlinePayment) -> Unit)) {
        onRefreshPaymentClickedListener = listener
    }

    private var onContactSupportClickedListener : ((OnlinePayment) -> Unit) ?= null
    fun setContactSupportClickedListener(listener: ((OnlinePayment) -> Unit)) {
        onContactSupportClickedListener = listener
    }


}