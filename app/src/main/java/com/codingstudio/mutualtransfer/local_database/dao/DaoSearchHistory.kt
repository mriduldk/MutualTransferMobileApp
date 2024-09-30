package com.codingstudio.mutualtransfer.local_database.dao

import androidx.room.*
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory
import com.codingstudio.mutualtransfer.model.search.SearchedType

@Dao
interface DaoSearchHistory {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(modelSearchHistory: ModelSearchHistory): Long

    @Update
    suspend fun update(modelSearchHistory: ModelSearchHistory): Int

    @Delete
    suspend fun delete(modelSearchHistory: ModelSearchHistory)

    @Query("DELETE FROM search_history")
    suspend fun deleteAll()

    @Query("SELECT * FROM search_history WHERE isDeleted = 0 ORDER by searchedTimeInLong DESC")
    suspend fun getAllSearches(): List<ModelSearchHistory>

    @Query("SELECT * FROM search_history WHERE searchedType = :searchedType and isDeleted = 0 ORDER by searchedTimeInLong DESC")
    suspend fun getSearchByType(searchedType : SearchedType): List<ModelSearchHistory>

}