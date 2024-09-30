package com.codingstudio.mutualtransfer.ui.search.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingstudio.mutualtransfer.R
import com.codingstudio.mutualtransfer.model.search.ModelSearchResult

class AdapterForSearchResult : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderForSearchResult(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewSearchResultText = itemView.findViewById<TextView>(R.id.textViewSearchResultText)

        fun bind(searchResult: ModelSearchResult) {

            textViewSearchResultText.text = searchResult.searchResultText

            textViewSearchResultText.setOnClickListener {
                onSearchedResultClickedListener?.let {
                    it(searchResult)
                }
            }

        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<ModelSearchResult>() {
        override fun areItemsTheSame(
            oldItem: ModelSearchResult,
            newItem: ModelSearchResult
        ): Boolean {
           return oldItem.searchResultId == newItem.searchResultId
        }

        override fun areContentsTheSame(
            oldItem: ModelSearchResult,
            newItem: ModelSearchResult
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderForSearchResult(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_layout_for_search_result, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val searchedHistory = differ.currentList[position]
        (holder as ViewHolderForSearchResult).bind(searchedHistory)
    }

    private var onSearchedResultClickedListener : ((ModelSearchResult) -> Unit) ?= null
    fun setOnSearchedResultClickedListener(listener: ((ModelSearchResult) -> Unit)) {
        onSearchedResultClickedListener = listener
    }


}