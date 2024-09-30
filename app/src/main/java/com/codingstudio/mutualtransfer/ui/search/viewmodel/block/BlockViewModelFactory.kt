package com.codingstudio.mutualtransfer.ui.search.viewmodel.block

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.remote.BlockRepository

class BlockViewModelFactory (private val application: Application, private val blockRepository: BlockRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlockViewModel(application, blockRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}