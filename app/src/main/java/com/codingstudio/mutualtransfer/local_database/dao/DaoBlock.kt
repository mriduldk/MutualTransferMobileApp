package com.codingstudio.mutualtransfer.local_database.dao

import androidx.room.*
import com.codingstudio.mutualtransfer.model.block.ModelBlock
import com.codingstudio.mutualtransfer.model.district.ModelDistrict

@Dao
interface DaoBlock {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(modelBlock: ModelBlock): Long

    @Update
    suspend fun update(modelBlock: ModelBlock): Int

    @Delete
    suspend fun delete(modelBlock: ModelBlock)

    @Query("DELETE FROM block")
    suspend fun deleteAll()

    @Query("SELECT * FROM block WHERE is_delete = 0 ORDER by block_name DESC")
    suspend fun getAllBlocks(): List<ModelBlock>

    @Query("SELECT * FROM block WHERE is_delete = 0 and district_id = :districtId ORDER by block_name DESC")
    suspend fun getAllBlocksByDistrictId(districtId: String): List<ModelBlock>

    @Query("SELECT * FROM block WHERE is_delete = 0 and district_name = :districtName ORDER by block_name DESC")
    suspend fun getAllBlocksByDistrictName(districtName: String): List<ModelBlock>


}