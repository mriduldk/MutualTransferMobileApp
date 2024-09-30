package com.codingstudio.mutualtransfer.model.payment

data class ResponsePaymentHistory (
    val status : Int,
    val message: String,
    val paymentHistory: PaymentHistory ?= null
)