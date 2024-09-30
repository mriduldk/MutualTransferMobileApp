package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.auth.ResponseLogin
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitAuthAPI {

    @POST("auth/checkUserPhoneNumber")
    @FormUrlEncoded
    suspend fun checkUserPhoneNumber(
        @Field("phone")
        phone : String,
    ): Response<ResponseLogin>


    @POST("auth/otpVerify")
    @FormUrlEncoded
    suspend fun otpVerify(
        @Field("fcm_token")
        fcm_token : String,
        @Field("otp")
        otp : String,
        @Field("phone")
        phone : String,
    ): Response<ResponseLogin>


}