package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.auth.ResponseLogin
import com.codingstudio.mutualtransfer.model.block.ResponseBlock
import com.codingstudio.mutualtransfer.model.district.ResponseDistrict
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitBlockAPI {

    @POST("block/GetAllBlocks")
    suspend fun getAllBlocks(): Response<ResponseBlock>


    @POST("block/GetBlocksByDistrict")
    @FormUrlEncoded
    suspend fun getBlocksByDistrict(
        @Field("district_id")
        district_id : String,
    ): Response<ResponseBlock>


    @POST("block/GetBlocksByDistrictAndBlockName")
    @FormUrlEncoded
    suspend fun getBlocksByDistrictAndBlockName(
        @Field("block_name")
        block_name : String,
        @Field("district_id")
        district_id : String,
    ): Response<ResponseBlock>


    @POST("block/GetBlocksByState")
    @FormUrlEncoded
    suspend fun getBlocksByState(
        @Field("state_id")
        state_id : String,
    ): Response<ResponseBlock>


}