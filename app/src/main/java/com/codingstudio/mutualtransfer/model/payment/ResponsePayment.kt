package com.codingstudio.mutualtransfer.model.payment

data class ResponsePayment (
    val status : Int,
    val message: String,
    val payment: Payment ?= null
)