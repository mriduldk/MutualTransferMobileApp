package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance


class WalletRepository {

    suspend fun getWalletOffers() = RetrofitInstance.walletAPI.getWalletOffers()

    suspend fun getWalletDataByUser(user_id: String) = RetrofitInstance.walletAPI.getWalletDataByUser(user_id = user_id)


}