package com.codingstudio.mutualtransfer.ui.search.viewmodel.block

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.block.ResponseBlock
import com.codingstudio.mutualtransfer.repository.remote.BlockRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockViewModel @Inject constructor(
    private val application: Application,
    private val blockRepository: BlockRepository
) : ViewModel() {


    private val _getAllBlocksObserver: MutableLiveData<EventWrapper<Resource<ResponseBlock>>> =
        MutableLiveData()
    val getAllBlocksObserver: LiveData<EventWrapper<Resource<ResponseBlock>>>
        get() = _getAllBlocksObserver

    fun getAllBlocksFun() = viewModelScope.launch {

        _getAllBlocksObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = blockRepository.getAllBlocks()
            HandleNetworkResponse.Check(response, _getAllBlocksObserver).process()
        } else {
            _getAllBlocksObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _getBlocksByDistrictObserver: MutableLiveData<EventWrapper<Resource<ResponseBlock>>> =
        MutableLiveData()
    val getBlocksByDistrictObserver: LiveData<EventWrapper<Resource<ResponseBlock>>>
        get() = _getBlocksByDistrictObserver

    fun getBlocksByDistrictFun(
        district_id: String,
    ) = viewModelScope.launch {

        _getBlocksByDistrictObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = blockRepository.getBlocksByDistrict(
                district_id = district_id
            )
            HandleNetworkResponse.Check(response, _getBlocksByDistrictObserver).process()
        } else {
            _getBlocksByDistrictObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _getBlocksByDistrictAndBlockNameObserver: MutableLiveData<EventWrapper<Resource<ResponseBlock>>> =
        MutableLiveData()
    val getBlocksByDistrictAndBlockNameObserver: LiveData<EventWrapper<Resource<ResponseBlock>>>
        get() = _getBlocksByDistrictAndBlockNameObserver

    fun getBlocksByDistrictAndBlockNameFun(
        block_name: String,
        district_id: String,
    ) = viewModelScope.launch {

        _getBlocksByDistrictAndBlockNameObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = blockRepository.getBlocksByDistrictAndBlockName(
                block_name = block_name,
                district_id = district_id
            )
            HandleNetworkResponse.Check(response, _getBlocksByDistrictAndBlockNameObserver).process()
        } else {
            _getBlocksByDistrictAndBlockNameObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _getBlocksByStateObserver: MutableLiveData<EventWrapper<Resource<ResponseBlock>>> =
        MutableLiveData()
    val getBlocksByStateObserver: LiveData<EventWrapper<Resource<ResponseBlock>>>
        get() = _getBlocksByStateObserver

    fun getBlocksByStateFun(
        state_id: String,
    ) = viewModelScope.launch {

        _getBlocksByStateObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = blockRepository.getBlocksByState(
                state_id = state_id
            )
            HandleNetworkResponse.Check(response, _getBlocksByStateObserver).process()
        } else {
            _getBlocksByStateObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }







}