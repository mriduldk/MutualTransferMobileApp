package com.codingstudio.mutualtransfer.ui.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.model.coin_transactions.CoinTransaction
import com.codingstudio.mutualtransfer.model.coin_transactions.EnumTransactionType
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory
import com.codingstudio.mutualtransfer.model.search.ModelSearchResultOfPerson

class AdapterForCoinTransaction(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderForCoinTransaction(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewTransition = itemView.findViewById<View>(R.id.viewTransition)
        private val textViewTransactionType = itemView.findViewById<TextView>(R.id.textViewTransactionType)
        private val textViewTransactionMessage = itemView.findViewById<TextView>(R.id.textViewTransactionMessage)
        private val textViewTransactionCategory = itemView.findViewById<TextView>(R.id.textViewTransactionCategory)

        fun bind(coinTransaction: CoinTransaction) {

            textViewTransactionType.text = coinTransaction.transaction_type
            textViewTransactionMessage.text = coinTransaction.transaction_message
            textViewTransactionCategory.text = coinTransaction.transaction_category

            when (coinTransaction.transaction_type) {
                EnumTransactionType.CREDIT.toString() -> {
                    textViewTransactionType.setTextColor(ContextCompat.getColorStateList(context, R.color.credit_color))
                    viewTransition.backgroundTintList = ContextCompat.getColorStateList(context, R.color.credit_color)
                }
                EnumTransactionType.DEBIT.toString() -> {
                    textViewTransactionType.setTextColor(ContextCompat.getColorStateList(context, R.color.debit_color))
                    viewTransition.backgroundTintList = ContextCompat.getColorStateList(context, R.color.debit_color)
                }
                else -> {
                    textViewTransactionType.setTextColor(ContextCompat.getColorStateList(context, R.color.darkTextColor))
                    viewTransition.backgroundTintList = ContextCompat.getColorStateList(context, R.color.darkTextColor)
                }
            }


            itemView.setOnClickListener {
                onCoinTransactionClickedListener?.let {
                    it(coinTransaction)
                }
            }

        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<CoinTransaction>() {
        override fun areItemsTheSame(
            oldItem: CoinTransaction,
            newItem: CoinTransaction
        ): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CoinTransaction,
            newItem: CoinTransaction
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderForCoinTransaction(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_coin_transaction, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val searchedHistory = differ.currentList[position]
        (holder as ViewHolderForCoinTransaction).bind(searchedHistory)
    }

    private var onCoinTransactionClickedListener : ((CoinTransaction) -> Unit) ?= null
    fun setOnCoinTransactionClickedListener(listener: ((CoinTransaction) -> Unit)) {
        onCoinTransactionClickedListener = listener
    }


}