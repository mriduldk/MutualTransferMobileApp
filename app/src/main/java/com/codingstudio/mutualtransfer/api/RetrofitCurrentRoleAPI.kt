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




    @POST("currentRole/getByUserTypeId")
    @FormUrlEncoded
    suspend fun getByUserTypeId(
        @Field("user_type_id")
        user_type_id: String
    ): Response<ResponseCurrentRole>




    @POST("currentRole/getByUserTypeAndCurrentRoleName")
    @FormUrlEncoded
    suspend fun getByUserTypeAndCurrentRoleName(
        @Field("user_type_id")
        user_type_id: String,
        @Field("current_role_name")
        current_role_name: String
    ): Response<ResponseCurrentRole>


}