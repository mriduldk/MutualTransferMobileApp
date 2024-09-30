package com.codingstudio.mutualtransfer.repository.local

import com.codingstudio.mutualtransfer.local_database.dao.DaoRecentlyViewed
import com.codingstudio.mutualtransfer.local_database.dao.DaoSearchHistory
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewed
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory
import com.codingstudio.mutualtransfer.model.search.SearchedType

class RecentlyViewedRepository(private val daoRecentlyViewed: DaoRecentlyViewed) {

    suspend fun insert(modelRecentlyViewed: ModelRecentlyViewed) = daoRecentlyViewed.insert(modelRecentlyViewed = modelRecentlyViewed)

    suspend fun update(modelRecentlyViewed: ModelRecentlyViewed) = daoRecentlyViewed.update(modelRecentlyViewed = modelRecentlyViewed)

    suspend fun delete(modelRecentlyViewed: ModelRecentlyViewed) = daoRecentlyViewed.delete(modelRecentlyViewed = modelRecentlyViewed)

    suspend fun deleteAll() = daoRecentlyViewed.deleteAll()

    suspend fun getAllRecentlyViewedPerson() = daoRecentlyViewed.getAllRecentlyViewedPerson()

    suspend fun getTopRecentlyViewedPerson() = daoRecentlyViewed.getTopRecentlyViewedPerson()

}