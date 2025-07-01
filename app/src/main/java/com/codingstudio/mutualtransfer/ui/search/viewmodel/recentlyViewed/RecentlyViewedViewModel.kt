package com.codingstudio.mutualtransfer.ui.search.viewmodel.recentlyViewed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewed
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewedNew
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedNewRepository
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyViewedViewModel @Inject constructor(
    private val recentlyViewedRepository: RecentlyViewedRepository,
    private val recentlyViewedNewRepository: RecentlyViewedNewRepository
) : ViewModel() {


    private val _insertObserver: MutableLiveData<Long> =
        MutableLiveData()
    val insertObserver: LiveData<Long>
        get() = _insertObserver

    fun insertFun(modelRecentlyViewed: ModelRecentlyViewed) = viewModelScope.launch {

        val response = recentlyViewedRepository.insert(modelRecentlyViewed = modelRecentlyViewed)
        _insertObserver.postValue(response)
    }


    private val _insertNewObserver: MutableLiveData<Long> =
        MutableLiveData()
    val insertNewObserver: LiveData<Long>
        get() = _insertObserver

    fun insertFun(modelRecentlyViewedNew: ModelRecentlyViewedNew) = viewModelScope.launch {

        val response = recentlyViewedNewRepository.insert(modelRecentlyViewed = modelRecentlyViewedNew)
        _insertNewObserver.postValue(response)
    }


    private val _updateObserver: MutableLiveData<Int> =
        MutableLiveData()
    val updateObserver: LiveData<Int>
        get() = _updateObserver

    fun updateFun(modelRecentlyViewed: ModelRecentlyViewed) = viewModelScope.launch {

        val response = recentlyViewedRepository.update(modelRecentlyViewed = modelRecentlyViewed)
        _updateObserver.postValue(response)
    }


    private val _deleteObserver: MutableLiveData<Int> =
        MutableLiveData()
    val deleteObserver: LiveData<Int>
        get() = _deleteObserver

    fun deleteFun(modelRecentlyViewed: ModelRecentlyViewed) = viewModelScope.launch {

        recentlyViewedRepository.delete(modelRecentlyViewed = modelRecentlyViewed)
        _deleteObserver.postValue(1)
    }


    private val _deleteAllObserver: MutableLiveData<Int> =
        MutableLiveData()
    val deleteAllObserver: LiveData<Int>
        get() = _deleteAllObserver

    fun deleteAllFun() = viewModelScope.launch {

        recentlyViewedRepository.deleteAll()
        _deleteAllObserver.postValue(1)
    }


    private val _deleteAllNewObserver: MutableLiveData<Int> =
        MutableLiveData()
    val deleteAllNewObserver: LiveData<Int>
        get() = _deleteAllObserver

    fun deleteAllNewFun() = viewModelScope.launch {

        recentlyViewedNewRepository.deleteAll()
        _deleteAllNewObserver.postValue(1)
    }


    private val _getAllRecentlyViewedPersonObserver: MutableLiveData<List<ModelRecentlyViewed>> =
        MutableLiveData()
    val getAllRecentlyViewedPersonObserver: LiveData<List<ModelRecentlyViewed>>
        get() = _getAllRecentlyViewedPersonObserver

    fun getAllRecentlyViewedPersonFun() = viewModelScope.launch {

        val response = recentlyViewedRepository.getAllRecentlyViewedPerson()
        _getAllRecentlyViewedPersonObserver.postValue(response)
    }


    private val _getAllRecentlyViewedNewPersonObserver: MutableLiveData<List<ModelRecentlyViewedNew>> =
        MutableLiveData()
    val getAllRecentlyViewedNewPersonObserver: LiveData<List<ModelRecentlyViewedNew>>
        get() = _getAllRecentlyViewedNewPersonObserver

    fun getAllRecentlyViewedNewPersonFun() = viewModelScope.launch {

        val response = recentlyViewedNewRepository.getAllRecentlyViewedPerson()
        _getAllRecentlyViewedNewPersonObserver.postValue(response)
    }



    private val _getTopRecentlyViewedPersonObserver: MutableLiveData<List<ModelRecentlyViewed>> =
        MutableLiveData()
    val getTopRecentlyViewedPersonObserver: LiveData<List<ModelRecentlyViewed>>
        get() = _getTopRecentlyViewedPersonObserver

    fun getTopRecentlyViewedPersonFun() = viewModelScope.launch {

        val response = recentlyViewedRepository.getTopRecentlyViewedPerson()
        _getTopRecentlyViewedPersonObserver.postValue(response)
    }









}