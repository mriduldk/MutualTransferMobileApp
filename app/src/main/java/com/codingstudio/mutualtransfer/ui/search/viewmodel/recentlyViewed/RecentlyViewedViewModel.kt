package com.codingstudio.mutualtransfer.ui.search.viewmodel.recentlyViewed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewed
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyViewedViewModel @Inject constructor(
    private val recentlyViewedRepository: RecentlyViewedRepository
) : ViewModel() {


    private val _insertObserver: MutableLiveData<Long> =
        MutableLiveData()
    val insertObserver: LiveData<Long>
        get() = _insertObserver

    fun insertFun(modelRecentlyViewed: ModelRecentlyViewed) = viewModelScope.launch {

        val response = recentlyViewedRepository.insert(modelRecentlyViewed = modelRecentlyViewed)
        _insertObserver.postValue(response)
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


    private val _getAllRecentlyViewedPersonObserver: MutableLiveData<List<ModelRecentlyViewed>> =
        MutableLiveData()
    val getAllRecentlyViewedPersonObserver: LiveData<List<ModelRecentlyViewed>>
        get() = _getAllRecentlyViewedPersonObserver

    fun getAllRecentlyViewedPersonFun() = viewModelScope.launch {

        val response = recentlyViewedRepository.getAllRecentlyViewedPerson()
        _getAllRecentlyViewedPersonObserver.postValue(response)
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