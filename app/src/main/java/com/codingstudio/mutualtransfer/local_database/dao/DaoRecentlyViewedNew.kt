package com.codingstudio.mutualtransfer.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewedNew

@Dao
interface DaoRecentlyViewedNew {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(modelRecentlyViewed: ModelRecentlyViewedNew): Long

    @Update
    suspend fun update(modelRecentlyViewed: ModelRecentlyViewedNew): Int

    @Delete
    suspend fun delete(modelRecentlyViewed: ModelRecentlyViewedNew)

    @Query("DELETE FROM recently_viewed_new")
    suspend fun deleteAll()

    @Query("SELECT * FROM recently_viewed_new ORDER by created_on_local DESC")
    suspend fun getAllRecentlyViewedPerson(): List<ModelRecentlyViewedNew>

    @Query("SELECT * FROM recently_viewed_new ORDER by created_on_local DESC LIMIT 3")
    suspend fun getTopRecentlyViewedPerson(): List<ModelRecentlyViewedNew>

}