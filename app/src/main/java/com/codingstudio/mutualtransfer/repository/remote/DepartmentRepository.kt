package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class DepartmentRepository {

    suspend fun getByUserType(
        user_type: String,
    ) = RetrofitInstance.departmentAPI.getByUserType(
        user_type = user_type
    )


}