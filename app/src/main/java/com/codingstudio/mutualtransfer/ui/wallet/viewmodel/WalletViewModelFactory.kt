package com.codingstudio.mutualtransfer.ui.wallet.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.remote.WalletRepository

class WalletViewModelFactory (private val application: Application, private val walletRepository: WalletRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalletViewModel(application, walletRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}