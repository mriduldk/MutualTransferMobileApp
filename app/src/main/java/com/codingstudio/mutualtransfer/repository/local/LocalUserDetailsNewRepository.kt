package com.codingstudio.mutualtransfer.repository.local

import com.codingstudio.mutualtransfer.local_database.dao.DaoUserDetailsNew
import com.codingstudio.mutualtransfer.model.auth.UserDetailsNew

class LocalUserDetailsNewRepository(private val userDetailsNewDao : DaoUserDetailsNew) {

    suspend fun insert(userDetailsNew: UserDetailsNew) = userDetailsNewDao.insert(userDetailsNew = userDetailsNew)

    suspend fun update(userDetailsNew: UserDetailsNew) = userDetailsNewDao.update(userDetailsNew = userDetailsNew)

    suspend fun delete(userDetailsNew: UserDetailsNew) = userDetailsNewDao.delete(userDetailsNew = userDetailsNew)

    suspend fun deleteAll() = userDetailsNewDao.deleteAll()

    suspend fun getAllUsers() = userDetailsNewDao.getAllUsers()

    suspend fun getUserByUserId(user_id : String) = userDetailsNewDao.getUserByUserId(user_id = user_id)

    suspend fun getUserByUserDetailsId(user_details_new_id : String) = userDetailsNewDao.getUserByUserDetailsId(user_details_new_id = user_details_new_id)

}