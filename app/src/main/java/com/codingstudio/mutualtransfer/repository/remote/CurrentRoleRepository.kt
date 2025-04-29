package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class CurrentRoleRepository {

    suspend fun getByUserType(
        user_type: String,
    ) = RetrofitInstance.currentRoleAPI.getByUserType(
        user_type = user_type
    )


}