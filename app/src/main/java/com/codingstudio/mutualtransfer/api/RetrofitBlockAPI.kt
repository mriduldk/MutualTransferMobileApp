package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.block.ResponseBlock
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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