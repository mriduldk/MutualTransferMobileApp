package com.codingstudio.mutualtransfer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.auth.UserDetailsNew
import com.codingstudio.mutualtransfer.repository.local.LocalUserDetailsNewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalUserDetailsNewViewModel @Inject constructor(
    private val localUserDetailsNewRepository: LocalUserDetailsNewRepository
) : ViewModel() {


    private val _getUserDetailsByUserIdObserver: MutableLiveData<UserDetailsNew> =
        MutableLiveData()
    val getUserDetailsByUserIdObserver: LiveData<UserDetailsNew>
        get() = _getUserDetailsByUserIdObserver

    fun getUserDetailsByUserIdFun(
        user_id: String,
    ) = viewModelScope.launch {

        _getUserDetailsByUserIdObserver.postValue(localUserDetailsNewRepository.getUserByUserId(user_id = user_id))

    }


    private val _getUserDetailsByUserDetailsIdObserver: MutableLiveData<UserDetailsNew> =
        MutableLiveData()
    val getUserDetailsByUserDetailsIdObserver: LiveData<UserDetailsNew>
        get() = _getUserDetailsByUserDetailsIdObserver

    fun getUserDetailsByUserDetailsIdObserverFun(
        user_details_new_id: String,
    ) = viewModelScope.launch {

        _getUserDetailsByUserDetailsIdObserver.postValue(localUserDetailsNewRepository.getUserByUserDetailsId(user_details_new_id = user_details_new_id))

    }


    private val _deleteAllObserver: MutableLiveData<Int> =
        MutableLiveData()
    val deleteAllObserver: LiveData<Int>
        get() = _deleteAllObserver

    fun deleteAllFun() = viewModelScope.launch {
        localUserDetailsNewRepository.deleteAll()
        _deleteAllObserver.postValue(1)
    }




}