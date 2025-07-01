package com.codingstudio.mutualtransfer.ui.userDetails.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.local.LocalUserDetailsNewRepository
import com.codingstudio.mutualtransfer.repository.remote.UserDetailsNewRepository

class UserDetailsNewViewModelFactory (private val application: Application, private val userDetailsNewRepository: UserDetailsNewRepository, private val localUserDetailsNewRepository: LocalUserDetailsNewRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserDetailsNewViewModel(application, userDetailsNewRepository, localUserDetailsNewRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}