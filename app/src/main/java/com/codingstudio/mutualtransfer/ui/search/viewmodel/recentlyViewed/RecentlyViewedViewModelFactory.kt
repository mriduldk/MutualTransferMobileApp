package com.codingstudio.mutualtransfer.ui.search.viewmodel.recentlyViewed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.local.HistoryRepository
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedNewRepository
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedRepository

class RecentlyViewedViewModelFactory (private val recentlyViewedRepository: RecentlyViewedRepository, private val recentlyViewedNewRepository: RecentlyViewedNewRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecentlyViewedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecentlyViewedViewModel(recentlyViewedRepository, recentlyViewedNewRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}