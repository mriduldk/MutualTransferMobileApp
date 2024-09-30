package com.codingstudio.mutualtransfer.ui.search.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory

class AdapterForSearchHistory : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderForSearchHistory(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewSearchHistoryText = itemView.findViewById<TextView>(R.id.textViewSearchHistoryText)
        private val imageViewSearchHistoryImport = itemView.findViewById<ImageView>(R.id.imageViewSearchHistoryImport)

        fun bind(searchHistory: ModelSearchHistory) {

            textViewSearchHistoryText.text = searchHistory.searchedText

            imageViewSearchHistoryImport.setOnClickListener {
                onSearchedHistoryPickClickListener?.let {
                    it(searchHistory)
                }
            }

            textViewSearchHistoryText.setOnClickListener {
                onSearchedHistoryClickedListener?.let {
                    it(searchHistory)
                }
            }

        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<ModelSearchHistory>() {
        override fun areItemsTheSame(
            oldItem: ModelSearchHistory,
            newItem: ModelSearchHistory
        ): Boolean {
           return oldItem.searchHistoryId == newItem.searchHistoryId
        }

        override fun areContentsTheSame(
            oldItem: ModelSearchHistory,
            newItem: ModelSearchHistory
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderForSearchHistory(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_layout_for_search_history, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val searchedHistory = differ.currentList[position]
        (holder as ViewHolderForSearchHistory).bind(searchedHistory)
    }

    private var onSearchedHistoryClickedListener : ((ModelSearchHistory) -> Unit) ?= null
    fun setOnSearchedHistoryClickedListener(listener: ((ModelSearchHistory) -> Unit)) {
        onSearchedHistoryClickedListener = listener
    }

    private var onSearchedHistoryPickClickListener : ((ModelSearchHistory) -> Unit) ?= null
    fun setOnSearchedHistoryPickClickListener(listener : ((ModelSearchHistory) -> Unit)) {
        onSearchedHistoryPickClickListener = listener
    }

}