package com.codingstudio.mutualtransfer.model.payment

data class PaymentHistory(
    val payment_history_id: String,
    val fk_user_id: String,
    val paid_amount: Int,
    val status: String,
)
