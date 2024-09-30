package com.codingstudio.mutualtransfer.ui.userDetails.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.auth.ResponseUserDetails
import com.codingstudio.mutualtransfer.repository.local.LocalUserDetailsRepository
import com.codingstudio.mutualtransfer.repository.remote.UserDetailsRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val application: Application,
    private val userDetailsRepository: UserDetailsRepository,
    private val localUserDetailsRepository: LocalUserDetailsRepository
) : ViewModel() {


    private val _saveUserPersonalInformationObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetails>>> =
        MutableLiveData()
    val saveUserPersonalInformationObserver: LiveData<EventWrapper<Resource<ResponseUserDetails>>>
        get() = _saveUserPersonalInformationObserver

    fun saveUserPersonalInformationFun(
        name: String,
        user_id: String,
        email: String,
        gender: String
    ) = viewModelScope.launch {

        _saveUserPersonalInformationObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsRepository.saveUserPersonalInformation(
                name = name,
                user_id = user_id,
                email = email,
                gender = gender
            )

            if (response.isSuccessful){
                response.body()?.userDetails?.let {
                    localUserDetailsRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _saveUserPersonalInformationObserver).process()
        } else {
            _saveUserPersonalInformationObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _saveUserEmployeeDetailsObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetails>>> =
        MutableLiveData()
    val saveUserEmployeeDetailsObserver: LiveData<EventWrapper<Resource<ResponseUserDetails>>>
        get() = _saveUserEmployeeDetailsObserver

    fun saveUserEmployeeDetailsFun(
        employee_code: String,
        school_type: String,
        teacher_type: String,
        user_id: String,
        subject_type: String
    ) = viewModelScope.launch {

        _saveUserEmployeeDetailsObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsRepository.saveUserEmployeeDetails(
                employee_code = employee_code,
                school_type = school_type,
                teacher_type = teacher_type,
                user_id = user_id,
                subject_type = subject_type
            )

            if (response.isSuccessful){
                response.body()?.userDetails?.let {
                    localUserDetailsRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _saveUserEmployeeDetailsObserver).process()
        } else {
            _saveUserEmployeeDetailsObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




    private val _saveUserSchoolDetailsObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetails>>> =
        MutableLiveData()
    val saveUserSchoolDetailsObserver: LiveData<EventWrapper<Resource<ResponseUserDetails>>>
        get() = _saveUserSchoolDetailsObserver

    fun saveUserSchoolDetailsFun(
        school_address_block: String,
        school_address_district: String,
        school_address_pin: String,
        school_address_state: String,
        school_address_vill: String,
        school_name: String,
        udice_code: String,
        user_id: String,
        amalgamation: Int
    ) = viewModelScope.launch {

        _saveUserSchoolDetailsObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsRepository.saveUserSchoolDetails(
                school_address_block = school_address_block,
                school_address_district = school_address_district,
                school_address_pin = school_address_pin,
                school_address_state = school_address_state,
                school_address_vill = school_address_vill,
                school_name = school_name,
                udice_code = udice_code,
                user_id = user_id,
                amalgamation = amalgamation
            )

            if (response.isSuccessful){
                response.body()?.userDetails?.let {
                    localUserDetailsRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _saveUserSchoolDetailsObserver).process()
        } else {
            _saveUserSchoolDetailsObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




    private val _saveUserPreferredDistrictObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetails>>> =
        MutableLiveData()
    val saveUserPreferredDistrictObserver: LiveData<EventWrapper<Resource<ResponseUserDetails>>>
        get() = _saveUserPreferredDistrictObserver

    fun saveUserPreferredDistrictFun(
        preferred_district_1: String,
        preferred_district_2: String,
        preferred_district_3: String,
        user_id: String
    ) = viewModelScope.launch {

        _saveUserPreferredDistrictObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsRepository.saveUserPreferredDistrict(
                preferred_district_1 = preferred_district_1,
                preferred_district_2 = preferred_district_2,
                preferred_district_3 = preferred_district_3,
                user_id = user_id
            )

            if (response.isSuccessful){
                response.body()?.userDetails?.let {
                    localUserDetailsRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _saveUserPreferredDistrictObserver).process()
        } else {
            _saveUserPreferredDistrictObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




    private val _changeActivelyLookingStatusObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetails>>> =
        MutableLiveData()
    val changeActivelyLookingStatusObserver: LiveData<EventWrapper<Resource<ResponseUserDetails>>>
        get() = _changeActivelyLookingStatusObserver

    fun changeActivelyLookingStatusFun(
        is_actively_looking: Int,
        user_id: String,
    ) = viewModelScope.launch {

        _changeActivelyLookingStatusObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsRepository.changeActivelyLookingStatus(
                is_actively_looking = is_actively_looking,
                user_id = user_id
            )

            if (response.isSuccessful){
                response.body()?.userDetails?.let {
                    localUserDetailsRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _changeActivelyLookingStatusObserver).process()
        } else {
            _changeActivelyLookingStatusObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




    private val _useReferralCodeObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetails>>> =
        MutableLiveData()
    val useReferralCodeObserver: LiveData<EventWrapper<Resource<ResponseUserDetails>>>
        get() = _useReferralCodeObserver

    fun useReferralCodeFun(
        referral_code: String,
        user_id: String,
    ) = viewModelScope.launch {

        _useReferralCodeObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsRepository.useReferralCode(
                referral_code = referral_code,
                user_id = user_id
            )

            if (response.isSuccessful){
                response.body()?.userDetails?.let {
                    localUserDetailsRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _useReferralCodeObserver).process()
        } else {
            _useReferralCodeObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




    private val _getUserDetailsByIdObserver: MutableLiveData<EventWrapper<Resource<ResponseUserDetails>>> =
        MutableLiveData()
    val getUserDetailsByIdObserver: LiveData<EventWrapper<Resource<ResponseUserDetails>>>
        get() = _getUserDetailsByIdObserver

    fun getUserDetailsById(
        user_phone: String,
        user_id: String,
    ) = viewModelScope.launch {

        _getUserDetailsByIdObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = userDetailsRepository.getUserDetailsById(
                user_phone = user_phone,
                user_id = user_id
            )

            if (response.isSuccessful){
                response.body()?.userDetails?.let {
                    localUserDetailsRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _getUserDetailsByIdObserver).process()
        } else {
            _getUserDetailsByIdObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }







}