package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.auth.ResponseLogin
import com.codingstudio.mutualtransfer.model.block.ResponseBlock
import com.codingstudio.mutualtransfer.model.district.ResponseDistrict
import com.codingstudio.mutualtransfer.model.subject.ResponseSubject
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitSubjectAPI {

    @POST("subject/GetAllHighSecondarySubject")
    suspend fun getAllHighSecondarySubject(): Response<ResponseSubject>

}