package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.user_type.ResponseUserType
import retrofit2.Response
import retrofit2.http.POST

interface RetrofitUserTypeAPI {

    @POST("userType/getAllUserTypes")
    suspend fun getAllUserTypes(): Response<ResponseUserType>

}