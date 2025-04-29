package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.current_role.ResponseCurrentRole
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitCurrentRoleAPI {

    @POST("currentRole/getByUserType")
    @FormUrlEncoded
    suspend fun getByUserType(
        @Field("user_type")
        user_type: String
    ): Response<ResponseCurrentRole>


}