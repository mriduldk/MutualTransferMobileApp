package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class CurrentRoleRepository {

    suspend fun getByUserType(
        user_type: String,
    ) = RetrofitInstance.currentRoleAPI.getByUserType(
        user_type = user_type
    )

    suspend fun getByUserTypeId(
        user_type_id: String,
    ) = RetrofitInstance.currentRoleAPI.getByUserTypeId(
        user_type_id = user_type_id
    )

    suspend fun getByUserTypeAndCurrentRoleName(
        user_type_id: String,
        current_role_name: String,
    ) = RetrofitInstance.currentRoleAPI.getByUserTypeAndCurrentRoleName(
        user_type_id = user_type_id,
        current_role_name = current_role_name
    )


}