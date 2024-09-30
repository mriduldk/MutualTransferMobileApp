package com.codingstudio.mutualtransfer.ui.payment.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.remote.PaymentRepository

class PaymentViewModelFactory (private val application: Application, private val paymentRepository: PaymentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(application, paymentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}