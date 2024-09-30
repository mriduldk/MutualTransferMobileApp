package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class AuthRepository {

    suspend fun checkUserPhoneNumber(phone: String)
        = RetrofitInstance.authAPI.checkUserPhoneNumber(phone)


    suspend fun otpVerify(fcm_token: String, otp: String, phone: String)
        = RetrofitInstance.authAPI.otpVerify(fcm_token = fcm_token, otp = otp, phone = phone)


}