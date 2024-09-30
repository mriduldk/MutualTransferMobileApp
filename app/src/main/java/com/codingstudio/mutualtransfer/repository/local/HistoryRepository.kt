package com.codingstudio.mutualtransfer.repository.local

import com.codingstudio.mutualtransfer.local_database.dao.DaoSearchHistory
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory
import com.codingstudio.mutualtransfer.model.search.SearchedType

class HistoryRepository(private val searchHistory: DaoSearchHistory) {

    suspend fun insert(modelSearchHistory: ModelSearchHistory) = searchHistory.insert(modelSearchHistory = modelSearchHistory)

    suspend fun update(modelSearchHistory: ModelSearchHistory) = searchHistory.update(modelSearchHistory = modelSearchHistory)

    suspend fun delete(modelSearchHistory: ModelSearchHistory) = searchHistory.delete(modelSearchHistory = modelSearchHistory)

    suspend fun deleteAll() = searchHistory.deleteAll()

    suspend fun getAllSearches() = searchHistory.getAllSearches()

    suspend fun getSearchByType(searchedType : SearchedType) = searchHistory.getSearchByType(searchedType = searchedType)

}