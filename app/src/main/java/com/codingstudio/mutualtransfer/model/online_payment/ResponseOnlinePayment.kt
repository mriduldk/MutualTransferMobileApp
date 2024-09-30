package com.codingstudio.mutualtransfer.model.online_payment

data class ResponseOnlinePayment (
    val status : Int,
    val message: String,
    val onlinePayment: OnlinePayment ?= null,
    val onlinePayments: List<OnlinePayment> ?= null,
)