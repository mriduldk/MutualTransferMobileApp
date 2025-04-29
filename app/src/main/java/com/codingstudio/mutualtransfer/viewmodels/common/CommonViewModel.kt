package com.codingstudio.mutualtransfer.viewmodels.common

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.current_role.ResponseCurrentRole
import com.codingstudio.mutualtransfer.model.department.ResponseDepartment
import com.codingstudio.mutualtransfer.model.zone_division.ResponseZoneDivision
import com.codingstudio.mutualtransfer.repository.remote.CurrentRoleRepository
import com.codingstudio.mutualtransfer.repository.remote.DepartmentRepository
import com.codingstudio.mutualtransfer.repository.remote.ZoneDivisionRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val application: Application,
    private val currentRoleRepository: CurrentRoleRepository,
    private val departmentRepository: DepartmentRepository,
    private val zoneDivisionRepository: ZoneDivisionRepository
) : ViewModel() {


    private val _getCurrentRolesByUserTypeObserver: MutableLiveData<EventWrapper<Resource<ResponseCurrentRole>>> =
        MutableLiveData()
    val getCurrentRolesByUserTypeObserver: LiveData<EventWrapper<Resource<ResponseCurrentRole>>>
        get() = _getCurrentRolesByUserTypeObserver

    fun getCurrentRolesByUserTypeFun(
        user_type: String
    ) = viewModelScope.launch {

        _getCurrentRolesByUserTypeObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = currentRoleRepository.getByUserType(
                user_type = user_type
            )
            HandleNetworkResponse.Check(response, _getCurrentRolesByUserTypeObserver).process()
        } else {
            _getCurrentRolesByUserTypeObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }


    private val _getDepartmentsByUserTypeObserver: MutableLiveData<EventWrapper<Resource<ResponseDepartment>>> =
        MutableLiveData()
    val getDepartmentsByUserTypeObserver: LiveData<EventWrapper<Resource<ResponseDepartment>>>
        get() = _getDepartmentsByUserTypeObserver

    fun getDepartmentsByUserTypeFun(
        user_type: String
    ) = viewModelScope.launch {

        _getDepartmentsByUserTypeObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = departmentRepository.getByUserType(
                user_type = user_type
            )
            HandleNetworkResponse.Check(response, _getDepartmentsByUserTypeObserver).process()
        } else {
            _getDepartmentsByUserTypeObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }


    private val _getZoneDivisionByUserTypeObserver: MutableLiveData<EventWrapper<Resource<ResponseZoneDivision>>> =
        MutableLiveData()
    val getZoneDivisionByUserTypeObserver: LiveData<EventWrapper<Resource<ResponseZoneDivision>>>
        get() = _getZoneDivisionByUserTypeObserver

    fun getZoneDivisionByUserTypeFun(
        user_type: String
    ) = viewModelScope.launch {

        _getZoneDivisionByUserTypeObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = zoneDivisionRepository.getByUserType(
                user_type = user_type
            )
            HandleNetworkResponse.Check(response, _getZoneDivisionByUserTypeObserver).process()
        } else {
            _getZoneDivisionByUserTypeObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }







}