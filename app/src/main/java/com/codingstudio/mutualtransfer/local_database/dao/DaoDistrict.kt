package com.codingstudio.mutualtransfer.local_database.dao

import androidx.room.*
import com.codingstudio.mutualtransfer.model.district.ModelDistrict

@Dao
interface DaoDistrict {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(modelDistrict: ModelDistrict): Long

    @Update
    suspend fun update(modelDistrict: ModelDistrict): Int

    @Delete
    suspend fun delete(modelDistrict: ModelDistrict)

    @Query("DELETE FROM district")
    suspend fun deleteAll()

    @Query("SELECT * FROM district WHERE is_delete = 0 ORDER by district_name DESC")
    suspend fun getAllDistricts(): List<ModelDistrict>


}