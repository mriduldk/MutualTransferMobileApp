package com.codingstudio.mutualtransfer.viewmodels.common

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.mutualtransfer.repository.remote.CurrentRoleRepository
import com.codingstudio.mutualtransfer.repository.remote.DepartmentRepository
import com.codingstudio.mutualtransfer.repository.remote.UserTypeRepository
import com.codingstudio.mutualtransfer.repository.remote.ZoneDivisionRepository

class CommonViewModelFactory (
    private val application: Application,
    private val currentRoleRepository: CurrentRoleRepository,
    private val departmentRepository: DepartmentRepository,
    private val zoneDivisionRepository: ZoneDivisionRepository,
    private val userTypeRepository: UserTypeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommonViewModel(application, currentRoleRepository, departmentRepository, zoneDivisionRepository, userTypeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}