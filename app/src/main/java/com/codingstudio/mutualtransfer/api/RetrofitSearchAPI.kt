package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.search.ResponseSearchResult
import com.codingstudio.mutualtransfer.model.search.ResponseSearchedPerson
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitSearchAPI {

    //https://sport.bodoland.gov.in/test/MutualTransferAPI/public/api
    ///search/SearchPerson

    @POST("search/SearchPerson_v2")
    @FormUrlEncoded
    suspend fun searchPerson(
        @Field("school_address_district")
        school_address_district : String,
        @Field("user_id")
        user_id : String,
        @Field("school_address_block")
        school_address_block : String,
        @Field("school_address_vill")
        school_address_vill : String,
        @Field("school_name")
        school_name : String
    ): Response<ResponseSearchResult>


    @POST("search/ViewPersonDetails_v2")
    @FormUrlEncoded
    suspend fun viewPersonDetails(
        @Field("person_user_id")
        person_user_id : String,
        @Field("user_id")
        user_id : String
    ): Response<ResponseSearchedPerson>


}