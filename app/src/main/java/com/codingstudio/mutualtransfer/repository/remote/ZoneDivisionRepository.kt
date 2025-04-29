package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class ZoneDivisionRepository {

    suspend fun getByUserType(
        user_type: String,
    ) = RetrofitInstance.zoneDivisionAPI.getByUserType(
        user_type = user_type
    )


}