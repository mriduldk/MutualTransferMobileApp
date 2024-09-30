package com.codingstudio.mutualtransfer.ui.search.viewmodel.searchHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.local.HistoryRepository

class SearchedHistoryViewModelFactory (private val historyRepository: HistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchedHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchedHistoryViewModel(historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}