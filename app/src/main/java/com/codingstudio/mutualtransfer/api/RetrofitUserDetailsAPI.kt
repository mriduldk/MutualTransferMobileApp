package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.auth.ResponseLogin
import com.codingstudio.mutualtransfer.model.auth.ResponseUserDetails
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitUserDetailsAPI {

    @POST("userDetails/savePersonalInformation")
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
    ): Response<ResponseUserDetails>


    @POST("userDetails/SaveUserEmployeeDetails")
    @FormUrlEncoded
    suspend fun saveUserEmployeeDetails(
        @Field("employee_code")
        employee_code : String,
        @Field("school_type")
        school_type : String,
        @Field("teacher_type")
        teacher_type : String,
        @Field("subject_type")
        subject_type : String,
        @Field("user_id")
        user_id : String,
    ): Response<ResponseUserDetails>


    @POST("userDetails/SaveUserSchoolDetails")
    @FormUrlEncoded
    suspend fun saveUserSchoolDetails(
        @Field("school_address_block")
        school_address_block : String,
        @Field("school_address_district")
        school_address_district : String,
        @Field("school_address_pin")
        school_address_pin : String,
        @Field("school_address_state")
        school_address_state : String,
        @Field("school_address_vill")
        school_address_vill : String,
        @Field("school_name")
        school_name : String,
        @Field("udice_code")
        udice_code : String,
        @Field("user_id")
        user_id : String,
        @Field("amalgamation")
        amalgamation : Int,
    ): Response<ResponseUserDetails>


    @POST("userDetails/SaveUserPreferredDistrict")
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
    ): Response<ResponseUserDetails>


    @POST("userDetails/ChangeActivelyLookingStatus")
    @FormUrlEncoded
    suspend fun changeActivelyLookingStatus(
        @Field("is_actively_looking")
        is_actively_looking : Int,
        @Field("user_id")
        user_id : String
    ): Response<ResponseUserDetails>



    @POST("userDetails/UseReferralCode")
    @FormUrlEncoded
    suspend fun useReferralCode(
        @Field("referral_code")
        referral_code : String,
        @Field("user_id")
        user_id : String
    ): Response<ResponseUserDetails>


    @POST("userDetails/GetUserDetailsById")
    @FormUrlEncoded
    suspend fun getUserDetailsById(
        @Field("user_phone")
        user_phone : String,
        @Field("user_id")
        user_id : String
    ): Response<ResponseUserDetails>


}