package com.codingstudio.mutualtransfer.ui.auth.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.auth.ResponseLogin
import com.codingstudio.mutualtransfer.repository.local.LocalUserDetailsRepository
import com.codingstudio.mutualtransfer.repository.remote.AuthRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val application: Application,
    private val authRepository: AuthRepository,
    private val localUserDetailsRepository: LocalUserDetailsRepository
) : ViewModel() {

    private val _checkUserPhoneNumberObserver : MutableLiveData<EventWrapper<Resource<ResponseLogin>>> = MutableLiveData()
    val checkUserPhoneNumberObserver: LiveData<EventWrapper<Resource<ResponseLogin>>>
        get() = _checkUserPhoneNumberObserver

    fun checkUserPhoneNumberFun(phone: String) = viewModelScope.launch {

        _checkUserPhoneNumberObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)){

            val response = authRepository.checkUserPhoneNumber(phone = phone)
            HandleNetworkResponse.Check(response, _checkUserPhoneNumberObserver).process()
        }
        else{
            _checkUserPhoneNumberObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _otpVerifyObserver : MutableLiveData<EventWrapper<Resource<ResponseLogin>>> = MutableLiveData()
    val otpVerifyObserver: LiveData<EventWrapper<Resource<ResponseLogin>>>
        get() = _otpVerifyObserver

    fun otpVerifyFun(fcm_token: String, otp: String, phone: String) = viewModelScope.launch {

        _otpVerifyObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)){

            val response = authRepository.otpVerify(fcm_token = fcm_token, otp = otp, phone = phone)

            if (response.isSuccessful) {
                response.body()?.userDetails?.let {
                    localUserDetailsRepository.insert(it)
                }
            }

            HandleNetworkResponse.Check(response, _otpVerifyObserver).process()
        }
        else{
            _otpVerifyObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




}