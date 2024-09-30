package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.auth.ResponseLogin
import com.codingstudio.mutualtransfer.model.district.ResponseDistrict
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitDistrictAPI {

    @POST("district/GetAllDistricts")
    suspend fun getAllDistricts(): Response<ResponseDistrict>


    @POST("district/GetDistrictByName")
    @FormUrlEncoded
    suspend fun getDistrictByName(
        @Field("district_name")
        district_name : String,
    ): Response<ResponseDistrict>


    @POST("district/GetDistrictsByStateAndDistrictName")
    @FormUrlEncoded
    suspend fun getDistrictsByStateAndDistrictName(
        @Field("district_name")
        district_name : String,
        @Field("state_id")
        state_id : String,
    ): Response<ResponseDistrict>


    @POST("district/GetDistrictByState")
    @FormUrlEncoded
    suspend fun getDistrictByState(
        @Field("state_id")
        state_id : String,
    ): Response<ResponseDistrict>


}