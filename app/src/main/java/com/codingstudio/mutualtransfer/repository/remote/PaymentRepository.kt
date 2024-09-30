package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class PaymentRepository {

    suspend fun saveUserPayForAnotherUser(
        payment_done_by: String,
        payment_done_for: String,
        amount: String,
    ) = RetrofitInstance.paymentAPI.saveUserPayForAnotherUser(
        payment_done_by = payment_done_by,
        payment_done_for = payment_done_for,
        amount = amount
    )


    suspend fun savePaymentHistory(
        paid_amount: Int,
        status: String,
        user_id: String,
    ) = RetrofitInstance.paymentAPI.savePaymentHistory(
        paid_amount = paid_amount,
        status = status,
        user_id = user_id,
    )


    suspend fun createRazorPayOrder(
        user_id: String,
        coins: String,
        amount: String,
    ) = RetrofitInstance.paymentAPI.createRazorPayOrder(
        user_id = user_id,
        coins = coins,
        amount = amount,
    )



    suspend fun verifyRazorPayPayment(
        user_id: String,
        online_payment_id: String,
        razorpay_signature: String,
        razorpay_order_id: String,
        razorpay_payment_id: String,
    ) = RetrofitInstance.paymentAPI.verifyRazorPayPayment(
        user_id = user_id,
        online_payment_id = online_payment_id,
        razorpay_signature = razorpay_signature,
        razorpay_order_id = razorpay_order_id,
        razorpay_payment_id = razorpay_payment_id,
    )


    suspend fun refreshPaymentStatusWithOrderId(
        user_id: String,
        online_payment_id: String,
        razorpay_order_id: String,
    ) = RetrofitInstance.paymentAPI.refreshPaymentStatusWithOrderId(
        user_id = user_id,
        online_payment_id = online_payment_id,
        razorpay_order_id = razorpay_order_id,
    )


    suspend fun getUsersPaymentDetails(
        user_id: String,
    ) = RetrofitInstance.paymentAPI.getUsersPaymentDetails(
        user_id = user_id,
    )



}