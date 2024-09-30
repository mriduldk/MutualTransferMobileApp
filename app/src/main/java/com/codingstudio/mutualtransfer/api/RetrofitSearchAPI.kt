package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.auth.ResponseLogin
import com.codingstudio.mutualtransfer.model.auth.ResponseUserDetails
import com.codingstudio.mutualtransfer.model.payment.ResponsePayment
import com.codingstudio.mutualtransfer.model.payment.ResponsePaymentHistory
import com.codingstudio.mutualtransfer.model.search.ResponseSearchResult
import com.codingstudio.mutualtransfer.model.search.ResponseSearchedPerson
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitSearchAPI {

    //https://sport.bodoland.gov.in/test/MutualTransferAPI/public/api
    ///search/SearchPerson

    @POST("search/SearchPerson")
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


    @POST("search/ViewPersonDetails")
    @FormUrlEncoded
    suspend fun viewPersonDetails(
        @Field("person_user_id")
        person_user_id : String,
        @Field("user_id")
        user_id : String
    ): Response<ResponseSearchedPerson>


}