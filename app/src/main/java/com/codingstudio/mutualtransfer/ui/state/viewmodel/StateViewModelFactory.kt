package com.codingstudio.mutualtransfer.ui.state.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.remote.StateRepository

class StateViewModelFactory (private val application: Application, private val stateRepository: StateRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StateViewModel(application, stateRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}