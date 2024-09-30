package com.codingstudio.mutualtransfer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.auth.UserDetails
import com.codingstudio.mutualtransfer.repository.local.LocalUserDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalUserDetailsViewModel @Inject constructor(
    private val localUserDetailsRepository: LocalUserDetailsRepository
) : ViewModel() {


    private val _getUserDetailsByUserIdObserver: MutableLiveData<UserDetails> =
        MutableLiveData()
    val getUserDetailsByUserIdObserver: LiveData<UserDetails>
        get() = _getUserDetailsByUserIdObserver

    fun getUserDetailsByUserIdFun(
        user_id: String,
    ) = viewModelScope.launch {

        _getUserDetailsByUserIdObserver.postValue(localUserDetailsRepository.getUserByUserId(user_id = user_id))

    }


    private val _getUserDetailsByUserDetailsIdObserver: MutableLiveData<UserDetails> =
        MutableLiveData()
    val getUserDetailsByUserDetailsIdObserver: LiveData<UserDetails>
        get() = _getUserDetailsByUserDetailsIdObserver

    fun getUserDetailsByUserDetailsIdObserverFun(
        user_details_id: String,
    ) = viewModelScope.launch {

        _getUserDetailsByUserDetailsIdObserver.postValue(localUserDetailsRepository.getUserByUserDetailsId(user_details_id = user_details_id))

    }


    private val _deleteAllObserver: MutableLiveData<Int> =
        MutableLiveData()
    val deleteAllObserver: LiveData<Int>
        get() = _deleteAllObserver

    fun deleteAllFun() = viewModelScope.launch {
        localUserDetailsRepository.deleteAll()
        _deleteAllObserver.postValue(1)
    }




}