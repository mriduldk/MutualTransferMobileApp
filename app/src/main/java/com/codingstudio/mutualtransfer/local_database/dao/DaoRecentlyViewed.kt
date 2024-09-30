package com.codingstudio.mutualtransfer.local_database.dao

import androidx.room.*
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewed
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory
import com.codingstudio.mutualtransfer.model.search.SearchedType

@Dao
interface DaoRecentlyViewed {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(modelRecentlyViewed: ModelRecentlyViewed): Long

    @Update
    suspend fun update(modelRecentlyViewed: ModelRecentlyViewed): Int

    @Delete
    suspend fun delete(modelRecentlyViewed: ModelRecentlyViewed)

    @Query("DELETE FROM recently_viewed")
    suspend fun deleteAll()

    @Query("SELECT * FROM recently_viewed ORDER by created_on_local DESC")
    suspend fun getAllRecentlyViewedPerson(): List<ModelRecentlyViewed>

    @Query("SELECT * FROM recently_viewed ORDER by created_on_local DESC LIMIT 3")
    suspend fun getTopRecentlyViewedPerson(): List<ModelRecentlyViewed>

}