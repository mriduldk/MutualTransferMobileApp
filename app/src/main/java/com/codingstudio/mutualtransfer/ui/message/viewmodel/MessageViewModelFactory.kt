package com.codingstudio.mutualtransfer.ui.message.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.local.HistoryRepository
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedRepository
import com.codingstudio.mutualtransfer.repository.remote.MessageRepository

class MessageViewModelFactory (private val application: Application, private val messageRepository: MessageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageViewModel(application, messageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}