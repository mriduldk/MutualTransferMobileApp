package com.codingstudio.mutualtransfer.ui.search.viewmodel.district

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.district.ResponseDistrict
import com.codingstudio.mutualtransfer.repository.local.HistoryRepository
import com.codingstudio.mutualtransfer.repository.remote.DistrictRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DistrictViewModel @Inject constructor(
    private val application: Application,
    private val districtRepository: DistrictRepository
) : ViewModel() {


    private val _getAllDistrictsObserver: MutableLiveData<EventWrapper<Resource<ResponseDistrict>>> =
        MutableLiveData()
    val getAllDistrictsObserver: LiveData<EventWrapper<Resource<ResponseDistrict>>>
        get() = _getAllDistrictsObserver

    fun getAllDistrictsFun() = viewModelScope.launch {

        _getAllDistrictsObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = districtRepository.getAllDistricts()
            HandleNetworkResponse.Check(response, _getAllDistrictsObserver).process()
        } else {
            _getAllDistrictsObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _getDistrictByNameObserver: MutableLiveData<EventWrapper<Resource<ResponseDistrict>>> =
        MutableLiveData()
    val getDistrictByNameObserver: LiveData<EventWrapper<Resource<ResponseDistrict>>>
        get() = _getDistrictByNameObserver

    fun getDistrictByNameFun(
        district_name: String,
    ) = viewModelScope.launch {

        _getDistrictByNameObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = districtRepository.getDistrictByName(
                district_name = district_name
            )
            HandleNetworkResponse.Check(response, _getDistrictByNameObserver).process()
        } else {
            _getDistrictByNameObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _getDistrictsByStateAndDistrictNameObserver: MutableLiveData<EventWrapper<Resource<ResponseDistrict>>> =
        MutableLiveData()
    val getDistrictsByStateAndDistrictNameObserver: LiveData<EventWrapper<Resource<ResponseDistrict>>>
        get() = _getDistrictsByStateAndDistrictNameObserver

    fun getDistrictsByStateAndDistrictNameFun(
        district_name: String,
        state_id: String,
    ) = viewModelScope.launch {

        _getDistrictsByStateAndDistrictNameObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = districtRepository.getDistrictsByStateAndDistrictName(
                district_name = district_name,
                state_id = state_id
            )
            HandleNetworkResponse.Check(response, _getDistrictsByStateAndDistrictNameObserver).process()
        } else {
            _getDistrictsByStateAndDistrictNameObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _getDistrictByStateObserver: MutableLiveData<EventWrapper<Resource<ResponseDistrict>>> =
        MutableLiveData()
    val getDistrictByStateObserver: LiveData<EventWrapper<Resource<ResponseDistrict>>>
        get() = _getDistrictByStateObserver

    fun getDistrictByStateFun(
        state_id: String,
    ) = viewModelScope.launch {

        _getDistrictByStateObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = districtRepository.getDistrictByState(
                state_id = state_id
            )
            HandleNetworkResponse.Check(response, _getDistrictByStateObserver).process()
        } else {
            _getDistrictByStateObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }







}