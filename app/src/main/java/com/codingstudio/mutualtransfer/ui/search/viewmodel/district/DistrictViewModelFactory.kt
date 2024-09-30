package com.codingstudio.mutualtransfer.ui.search.viewmodel.district

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.local.HistoryRepository
import com.codingstudio.mutualtransfer.repository.remote.DistrictRepository

class DistrictViewModelFactory (private val application: Application, private val districtRepository: DistrictRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DistrictViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DistrictViewModel(application, districtRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}