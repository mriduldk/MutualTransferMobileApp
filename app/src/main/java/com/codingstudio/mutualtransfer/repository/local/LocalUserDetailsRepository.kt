package com.codingstudio.mutualtransfer.repository.local

import com.codingstudio.mutualtransfer.local_database.dao.DaoUserDetails
import com.codingstudio.mutualtransfer.model.auth.UserDetails

class LocalUserDetailsRepository(private val userDetailsDao : DaoUserDetails) {

    suspend fun insert(userDetails: UserDetails) = userDetailsDao.insert(userDetails = userDetails)

    suspend fun update(userDetails: UserDetails) = userDetailsDao.update(userDetails = userDetails)

    suspend fun delete(userDetails: UserDetails) = userDetailsDao.delete(userDetails = userDetails)

    suspend fun deleteAll() = userDetailsDao.deleteAll()

    suspend fun getAllUsers() = userDetailsDao.getAllUsers()

    suspend fun getUserByUserId(user_id : String) = userDetailsDao.getUserByUserId(user_id = user_id)

    suspend fun getUserByUserDetailsId(user_details_id : String) = userDetailsDao.getUserByUserDetailsId(user_details_id = user_details_id)

}