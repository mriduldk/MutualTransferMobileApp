package com.codingstudio.mutualtransfer.local_database.dao

import androidx.room.*
import com.codingstudio.mutualtransfer.model.auth.UserDetails
import com.codingstudio.mutualtransfer.model.block.ModelBlock
import com.codingstudio.mutualtransfer.model.district.ModelDistrict

@Dao
interface DaoUserDetails {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userDetails: UserDetails): Long

    @Update
    suspend fun update(userDetails: UserDetails): Int

    @Delete
    suspend fun delete(userDetails: UserDetails)

    @Query("DELETE FROM user_details")
    suspend fun deleteAll()

    @Query("SELECT * FROM user_details")
    suspend fun getAllUsers(): List<UserDetails>

    @Query("SELECT * FROM user_details WHERE fk_user_id = :user_id LIMIT 1")
    suspend fun getUserByUserId(user_id: String): UserDetails?

    @Query("SELECT * FROM user_details WHERE user_details_id = :user_details_id LIMIT 1")
    suspend fun getUserByUserDetailsId(user_details_id: String): UserDetails?


}