package com.codingstudio.mutualtransfer.ui.search.viewmodel.search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.remote.SearchRepository

class SearchViewModelFactory (private val application: Application, private val searchRepository: SearchRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(application, searchRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}