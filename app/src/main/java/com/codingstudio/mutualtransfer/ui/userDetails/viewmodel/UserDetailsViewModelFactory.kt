package com.codingstudio.mutualtransfer.ui.userDetails.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.local.LocalUserDetailsRepository
import com.codingstudio.mutualtransfer.repository.remote.UserDetailsRepository

class UserDetailsViewModelFactory (private val application: Application, private val userDetailsRepository: UserDetailsRepository, private val localUserDetailsRepository: LocalUserDetailsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserDetailsViewModel(application, userDetailsRepository, localUserDetailsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}