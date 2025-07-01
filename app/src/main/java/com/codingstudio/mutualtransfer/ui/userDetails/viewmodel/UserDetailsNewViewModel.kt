package com.codingstudio.mutualtransfer.ui.userDetails.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.auth.ResponseUserDetailsNew
import com.codingstudio.mutualtransfer.repository.local.LocalUserDetailsNewRepository
import com.codingstudio.mutualtransfer.repository.remote.UserDetailsNewRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsNewViewModel @Inject constructor(
    private val application: Application,
    private val userDetailsNewRepository: UserDetailsNewRepository,
    private val localUserDetailsNewRepository: LocalUserDetailsNewRepository
) : ViewModel() {


    private val _saveUserPersonalInformationObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetailsNew>>> =
        MutableLiveData()
    val saveUserPersonalInformationObserver: LiveData<EventWrapper<Resource<ResponseUserDetailsNew>>>
        get() = _saveUserPersonalInformationObserver

    fun saveUserPersonalInformationFun(
        name: String,
        user_id: String,
        email: String,
        gender: String,
        user_type: String,
        user_type_id: String
    ) = viewModelScope.launch {

        _saveUserPersonalInformationObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsNewRepository.saveUserPersonalInformation(
                name = name,
                user_id = user_id,
                email = email,
                gender = gender,
                user_type = user_type,
                user_type_id = user_type_id
            )

            if (response.isSuccessful){
                response.body()?.userDetailsNew?.let {
                    localUserDetailsNewRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _saveUserPersonalInformationObserver).process()
        } else {
            _saveUserPersonalInformationObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _saveUserJobDetailsObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetailsNew>>> =
        MutableLiveData()
    val saveUserJobDetailsObserver: LiveData<EventWrapper<Resource<ResponseUserDetailsNew>>>
        get() = _saveUserJobDetailsObserver

    fun saveUserJobDetailsFun(
        user_id: String,
        current_role: String,
        employee_code: String,
        department: String,
        zone_division: String,
        service_type: String,
        current_organisation_name: String,
        job_address_village: String,
        job_address_district: String,
        job_address_block: String,
        job_address_state: String,
        job_address_pin: String
    ) = viewModelScope.launch {

        _saveUserJobDetailsObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsNewRepository.saveUserJobDetails(
                user_id = user_id,
                current_role = current_role,
                employee_code = employee_code,
                department = department,
                zone_division = zone_division,
                service_type = service_type,
                current_organisation_name = current_organisation_name,
                job_address_village = job_address_village,
                job_address_district = job_address_district,
                job_address_block = job_address_block,
                job_address_state = job_address_state,
                job_address_pin = job_address_pin
            )

            if (response.isSuccessful){
                response.body()?.userDetailsNew?.let {
                    localUserDetailsNewRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _saveUserJobDetailsObserver).process()
        } else {
            _saveUserJobDetailsObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




    private val _saveUserPreferredDistrictObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetailsNew>>> =
        MutableLiveData()
    val saveUserPreferredDistrictObserver: LiveData<EventWrapper<Resource<ResponseUserDetailsNew>>>
        get() = _saveUserPreferredDistrictObserver

    fun saveUserPreferredDistrictFun(
        preferred_district_1: String,
        preferred_district_2: String,
        preferred_district_3: String,
        user_id: String
    ) = viewModelScope.launch {

        _saveUserPreferredDistrictObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsNewRepository.saveUserPreferredDistrict(
                preferred_district_1 = preferred_district_1,
                preferred_district_2 = preferred_district_2,
                preferred_district_3 = preferred_district_3,
                user_id = user_id
            )

            if (response.isSuccessful){
                response.body()?.userDetailsNew?.let {
                    localUserDetailsNewRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _saveUserPreferredDistrictObserver).process()
        } else {
            _saveUserPreferredDistrictObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




    private val _changeActivelyLookingStatusObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetailsNew>>> =
        MutableLiveData()
    val changeActivelyLookingStatusObserver: LiveData<EventWrapper<Resource<ResponseUserDetailsNew>>>
        get() = _changeActivelyLookingStatusObserver

    fun changeActivelyLookingStatusFun(
        is_actively_looking: Int,
        user_id: String,
    ) = viewModelScope.launch {

        _changeActivelyLookingStatusObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsNewRepository.changeActivelyLookingStatus(
                is_actively_looking = is_actively_looking,
                user_id = user_id
            )

            if (response.isSuccessful){
                response.body()?.userDetailsNew?.let {
                    localUserDetailsNewRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _changeActivelyLookingStatusObserver).process()
        } else {
            _changeActivelyLookingStatusObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




    private val _getUserDetailsByIdObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetailsNew>>> =
        MutableLiveData()
    val getUserDetailsByIdObserver: LiveData<EventWrapper<Resource<ResponseUserDetailsNew>>>
        get() = _getUserDetailsByIdObserver

    fun getUserDetailsByIdFun(
        user_phone: String,
        user_id: String,
    ) = viewModelScope.launch {

        _getUserDetailsByIdObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsNewRepository.getUserDetailsById(
                user_phone = user_phone,
                user_id = user_id
            )

            if (response.isSuccessful){
                response.body()?.userDetailsNew?.let {
                    localUserDetailsNewRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _getUserDetailsByIdObserver).process()
        } else {
            _getUserDetailsByIdObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }







}