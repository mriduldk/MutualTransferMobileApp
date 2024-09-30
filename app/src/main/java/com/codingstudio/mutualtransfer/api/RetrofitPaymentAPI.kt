package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.auth.ResponseLogin
import com.codingstudio.mutualtransfer.model.auth.ResponseUserDetails
import com.codingstudio.mutualtransfer.model.online_payment.ResponseOnlinePayment
import com.codingstudio.mutualtransfer.model.payment.ResponsePayment
import com.codingstudio.mutualtransfer.model.payment.ResponsePaymentHistory
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitPaymentAPI {

    @POST("payment/SaveUserPayForAnotherUser")
    @FormUrlEncoded
    suspend fun saveUserPayForAnotherUser(
        @Field("payment_done_by")
        payment_done_by : String,
        @Field("payment_done_for")
        payment_done_for : String,
        @Field("amount")
        amount : String
    ): Response<ResponsePayment>


    @POST("paymentHistory/savePaymentHistory")
    @FormUrlEncoded
    suspend fun savePaymentHistory(
        @Field("paid_amount")
        paid_amount : Int,
        @Field("status")
        status : String,
        @Field("user_id")
        user_id : String
    ): Response<ResponsePaymentHistory>


    @POST("onlinePayment/CreateOrder")
    @FormUrlEncoded
    suspend fun createRazorPayOrder(
        @Field("user_id")
        user_id : String,
        @Field("coins")
        coins : String,
        @Field("amount")
        amount : String
    ): Response<ResponseOnlinePayment>


    @POST("onlinePayment/VerifyPayment")
    @FormUrlEncoded
    suspend fun verifyRazorPayPayment(
        @Field("user_id")
        user_id : String,
        @Field("online_payment_id")
        online_payment_id : String,
        @Field("razorpay_signature")
        razorpay_signature : String,
        @Field("razorpay_order_id")
        razorpay_order_id : String,
        @Field("razorpay_payment_id")
        razorpay_payment_id : String
    ): Response<ResponseOnlinePayment>


    @POST("onlinePayment/RefreshPaymentStatusWithOrderId")
    @FormUrlEncoded
    suspend fun refreshPaymentStatusWithOrderId(
        @Field("user_id")
        user_id : String,
        @Field("online_payment_id")
        online_payment_id : String,
        @Field("razorpay_order_id")
        razorpay_order_id : String
    ): Response<ResponseOnlinePayment>


    @POST("onlinePayment/GetUsersPaymentDetails")
    @FormUrlEncoded
    suspend fun getUsersPaymentDetails(
        @Field("user_id")
        user_id : String
    ): Response<ResponseOnlinePayment>


}