package com.codingstudio.mutualtransfer.ui.state.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.state.ResponseState
import com.codingstudio.mutualtransfer.repository.remote.StateRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StateViewModel @Inject constructor(
    private val application: Application,
    private val stateRepository: StateRepository
) : ViewModel() {


    private val _getAllStatesObserver: MutableLiveData<EventWrapper<Resource<ResponseState>>> =
        MutableLiveData()
    val getAllStatesObserver: LiveData<EventWrapper<Resource<ResponseState>>>
        get() = _getAllStatesObserver

    fun getAllStatesFun() = viewModelScope.launch {

        _getAllStatesObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = stateRepository.getAllStates()
            HandleNetworkResponse.Check(response, _getAllStatesObserver).process()
        } else {
            _getAllStatesObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _getStateByNameObserver: MutableLiveData<EventWrapper<Resource<ResponseState>>> =
        MutableLiveData()
    val getStateByNameObserver: LiveData<EventWrapper<Resource<ResponseState>>>
        get() = _getStateByNameObserver

    fun getStateByNameFun(
        state_name: String,
    ) = viewModelScope.launch {

        _getStateByNameObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = stateRepository.getStateByName(
                state_name = state_name
            )
            HandleNetworkResponse.Check(response, _getStateByNameObserver).process()
        } else {
            _getStateByNameObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




}