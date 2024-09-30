package com.codingstudio.mutualtransfer.model.payment

data class Payment(
    val payment_id: String,
    val payment_done_by: String,
    val payment_done_for: String,
    val amount: String,
)
