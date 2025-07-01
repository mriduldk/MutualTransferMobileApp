package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class UserTypeRepository {

    suspend fun getAllUserTypes() = RetrofitInstance.userTypeAPI.getAllUserTypes()


}