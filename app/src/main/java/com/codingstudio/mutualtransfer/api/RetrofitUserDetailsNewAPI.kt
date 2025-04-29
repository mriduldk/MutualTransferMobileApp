package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.auth.ResponseUserDetailsNew
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitUserDetailsNewAPI {

    @POST("userDetailsNew/savePersonalInformation")
    @FormUrlEncoded
    suspend fun saveUserPersonalInformation(
        @Field("name")
        name : String,
        @Field("user_id")
        user_id : String,
        @Field("email")
        email : String,
        @Field("gender")
        gender : String,
        @Field("user_type")
        user_type : String,
    ): Response<ResponseUserDetailsNew>


    @POST("userDetailsNew/SaveUserJobDetails")
    @FormUrlEncoded
    suspend fun saveUserJobDetails(
        @Field("user_id")
        user_id : String,
        @Field("current_role")
        current_role : String,
        @Field("employee_code")
        employee_code : String,
        @Field("department")
        department : String,
        @Field("zone_division")
        zone_division : String,
        @Field("service_type")
        service_type : String,
        @Field("current_organisation_name")
        current_organisation_name : String,
        @Field("job_address_village")
        job_address_village : String,
        @Field("job_address_district")
        job_address_district : String,
        @Field("job_address_block")
        job_address_block : String,
        @Field("job_address_state")
        job_address_state : String,
        @Field("job_address_pin")
        job_address_pin : String,
    ): Response<ResponseUserDetailsNew>


    @POST("userDetailsNew/SaveUserPreferredDistrict")
    @FormUrlEncoded
    suspend fun saveUserPreferredDistrict(
        @Field("preferred_district_1")
        preferred_district_1 : String,
        @Field("preferred_district_2")
        preferred_district_2 : String,
        @Field("preferred_district_3")
        preferred_district_3 : String,
        @Field("user_id")
        user_id : String,
    ): Response<ResponseUserDetailsNew>


    @POST("userDetailsNew/ChangeActivelyLookingStatus")
    @FormUrlEncoded
    suspend fun changeActivelyLookingStatus(
        @Field("is_actively_looking")
        is_actively_looking : Int,
        @Field("user_id")
        user_id : String
    ): Response<ResponseUserDetailsNew>



    @POST("userDetailsNew/GetUserDetailsById")
    @FormUrlEncoded
    suspend fun getUserDetailsById(
        @Field("user_phone")
        user_phone : String,
        @Field("user_id")
        user_id : String
    ): Response<ResponseUserDetailsNew>


}