package com.codingstudio.mutualtransfer.ui.search.viewmodel.searchHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory
import com.codingstudio.mutualtransfer.model.search.SearchedType
import com.codingstudio.mutualtransfer.repository.local.HistoryRepository
import kotlinx.coroutines.launch

class SearchedHistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {


    private val _insertHistoryObserver: MutableLiveData<Long> =
        MutableLiveData()
    val insertHistoryObserver: LiveData<Long>
        get() = _insertHistoryObserver

    fun insertHistoryFun(modelSearchHistory: ModelSearchHistory) = viewModelScope.launch {

        val response = historyRepository.insert(modelSearchHistory = modelSearchHistory)
        _insertHistoryObserver.postValue(response)
    }


    private val _updateHistoryObserver: MutableLiveData<Int> =
        MutableLiveData()
    val updateHistoryObserver: LiveData<Int>
        get() = _updateHistoryObserver

    fun updateHistoryFun(modelSearchHistory: ModelSearchHistory) = viewModelScope.launch {

        val response = historyRepository.update(modelSearchHistory = modelSearchHistory)
        _updateHistoryObserver.postValue(response)
    }


    private val _deleteHistoryObserver: MutableLiveData<Int> =
        MutableLiveData()
    val deleteHistoryObserver: LiveData<Int>
        get() = _deleteHistoryObserver

    fun deleteHistoryFun(modelSearchHistory: ModelSearchHistory) = viewModelScope.launch {

        historyRepository.delete(modelSearchHistory = modelSearchHistory)
        _deleteHistoryObserver.postValue(1)
    }


    private val _deleteAllHistoryObserver: MutableLiveData<Int> =
        MutableLiveData()
    val deleteAllHistoryObserver: LiveData<Int>
        get() = _deleteAllHistoryObserver

    fun deleteAllHistoryFun() = viewModelScope.launch {

        historyRepository.deleteAll()
        _deleteAllHistoryObserver.postValue(1)
    }


    private val _getAllSearchesHistoryObserver: MutableLiveData<List<ModelSearchHistory>> =
        MutableLiveData()
    val getAllSearchesHistoryObserver: LiveData<List<ModelSearchHistory>>
        get() = _getAllSearchesHistoryObserver

    fun getAllSearchesHistoryFun() = viewModelScope.launch {

        val response = historyRepository.getAllSearches()
        _getAllSearchesHistoryObserver.postValue(response)
    }


    private val _getSearchByTypeHistoryObserver: MutableLiveData<List<ModelSearchHistory>> =
        MutableLiveData()
    val getSearchByTypeHistoryObserver: LiveData<List<ModelSearchHistory>>
        get() = _getSearchByTypeHistoryObserver

    fun getSearchByTypeHistoryFun(searchedType : SearchedType) = viewModelScope.launch {

        val response = historyRepository.getSearchByType(searchedType = searchedType)
        _getSearchByTypeHistoryObserver.postValue(response)
    }









}