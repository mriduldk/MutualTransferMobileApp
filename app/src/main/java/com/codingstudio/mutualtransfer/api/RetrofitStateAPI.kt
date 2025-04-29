package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.state.ResponseState
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitStateAPI {

    @POST("state/GetAllStates")
    suspend fun getAllStates(): Response<ResponseState>


    @POST("district/GetStateByName")
    @FormUrlEncoded
    suspend fun getStateByName(
        @Field("state_name")
        state_name: String
    ): Response<ResponseState>


}