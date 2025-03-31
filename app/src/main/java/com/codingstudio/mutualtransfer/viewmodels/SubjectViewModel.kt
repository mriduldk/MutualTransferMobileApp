package com.codingstudio.mutualtransfer.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.block.ResponseBlock
import com.codingstudio.mutualtransfer.model.subject.ResponseSubject
import com.codingstudio.mutualtransfer.repository.remote.BlockRepository
import com.codingstudio.mutualtransfer.repository.remote.SubjectRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val application: Application,
    private val subjectRepository: SubjectRepository
) : ViewModel() {


    private val _getAllHighSecondarySubjectObserver: MutableLiveData<EventWrapper<Resource<ResponseSubject>>> =
        MutableLiveData()
    val getAllHighSecondarySubjectObserver: LiveData<EventWrapper<Resource<ResponseSubject>>>
        get() = _getAllHighSecondarySubjectObserver

    fun getAllHighSecondarySubjectFun() = viewModelScope.launch {

        _getAllHighSecondarySubjectObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = subjectRepository.getAllHighSecondarySubject()
            HandleNetworkResponse.Check(response, _getAllHighSecondarySubjectObserver).process()
        } else {
            _getAllHighSecondarySubjectObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }

}