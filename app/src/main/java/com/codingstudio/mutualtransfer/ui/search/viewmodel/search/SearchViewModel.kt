package com.codingstudio.mutualtransfer.ui.search.viewmodel.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.search.ResponseSearchResult
import com.codingstudio.mutualtransfer.model.search.ResponseSearchedPerson
import com.codingstudio.mutualtransfer.repository.remote.SearchRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import kotlinx.coroutines.launch

class SearchViewModel(
    private val application: Application,
    private val searchRepository: SearchRepository
) : ViewModel() {


    private val _searchPersonObserver: MutableLiveData<EventWrapper<Resource<ResponseSearchResult>>> =
        MutableLiveData()
    val searchPersonObserver: LiveData<EventWrapper<Resource<ResponseSearchResult>>>
        get() = _searchPersonObserver

    fun searchPersonFun(

        school_address_district: String,
        user_id: String,
        school_address_block: String,
        school_address_vill: String,
        school_name: String,
    ) = viewModelScope.launch {

        _searchPersonObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = searchRepository.searchPerson(
                school_address_district = school_address_district,
                user_id = user_id,
                school_address_block = school_address_block,
                school_address_vill = school_address_vill,
                school_name = school_name
            )
            HandleNetworkResponse.Check(response, _searchPersonObserver).process()
        } else {
            _searchPersonObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _viewPersonDetailsObserver: MutableLiveData<EventWrapper<Resource<ResponseSearchedPerson>>> =
        MutableLiveData()
    val viewPersonDetailsObserver: LiveData<EventWrapper<Resource<ResponseSearchedPerson>>>
        get() = _viewPersonDetailsObserver

    fun viewPersonDetailsFun(
        person_user_id: String,
        user_id: String,
    ) = viewModelScope.launch {

        _viewPersonDetailsObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = searchRepository.viewPersonDetails(
                person_user_id = person_user_id,
                user_id = user_id,
            )
            HandleNetworkResponse.Check(response, _viewPersonDetailsObserver).process()
        } else {
            _viewPersonDetailsObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }






}