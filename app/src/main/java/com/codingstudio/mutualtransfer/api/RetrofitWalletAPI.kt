package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.auth.ResponseUserDetails
import com.codingstudio.mutualtransfer.model.wallet.ResponseWallet
import com.codingstudio.mutualtransfer.model.wallet.ResponseWalletOffers
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitWalletAPI {

    @POST("walletOffer/GetWalletOffers")
    suspend fun getWalletOffers(): Response<ResponseWalletOffers>


    @POST("wallet/GetWalletDataByUser")
    @FormUrlEncoded
    suspend fun getWalletDataByUser(
        @Field("user_id")
        user_id : String
    ): Response<ResponseWallet>



}