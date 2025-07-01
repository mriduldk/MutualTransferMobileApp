package com.codingstudio.mutualtransfer.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.codingstudio.mutualtransfer.model.auth.UserDetailsNew

@Dao
interface DaoUserDetailsNew {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userDetailsNew: UserDetailsNew): Long

    @Update
    suspend fun update(userDetailsNew: UserDetailsNew): Int

    @Delete
    suspend fun delete(userDetailsNew: UserDetailsNew)

    @Query("DELETE FROM user_details_new")
    suspend fun deleteAll()

    @Query("SELECT * FROM user_details_new")
    suspend fun getAllUsers(): List<UserDetailsNew>

    @Query("SELECT * FROM user_details_new WHERE fk_user_id = :user_id LIMIT 1")
    suspend fun getUserByUserId(user_id: String): UserDetailsNew?

    @Query("SELECT * FROM user_details_new WHERE user_details_new_id = :user_details_new_id LIMIT 1")
    suspend fun getUserByUserDetailsId(user_details_new_id: String): UserDetailsNew?


}