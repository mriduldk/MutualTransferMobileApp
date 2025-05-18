package com.codingstudio.mutualtransfer.repository.local

import com.codingstudio.mutualtransfer.local_database.dao.DaoRecentlyViewedNew
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewedNew

class RecentlyViewedNewRepository(private val daoRecentlyViewed: DaoRecentlyViewedNew) {

    suspend fun insert(modelRecentlyViewed: ModelRecentlyViewedNew) = daoRecentlyViewed.insert(modelRecentlyViewed = modelRecentlyViewed)

    suspend fun update(modelRecentlyViewed: ModelRecentlyViewedNew) = daoRecentlyViewed.update(modelRecentlyViewed = modelRecentlyViewed)

    suspend fun delete(modelRecentlyViewed: ModelRecentlyViewedNew) = daoRecentlyViewed.delete(modelRecentlyViewed = modelRecentlyViewed)

    suspend fun deleteAll() = daoRecentlyViewed.deleteAll()

    suspend fun getAllRecentlyViewedPerson() = daoRecentlyViewed.getAllRecentlyViewedPerson()

    suspend fun getTopRecentlyViewedPerson() = daoRecentlyViewed.getTopRecentlyViewedPerson()

}