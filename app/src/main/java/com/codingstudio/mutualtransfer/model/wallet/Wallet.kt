package com.codingstudio.mutualtransfer.model.wallet

import com.codingstudio.mutualtransfer.model.coin_transactions.CoinTransaction

data class Wallet(
    val id: Int,
    val wallet_id: String ?= null,
    val fk_user_id: String ?= null,
    val total_amount: Int ?= null,
    val expired_on: String ?= null,
    val created_at: String ?= null,
    val updated_at: String ?= null,
    val coin_transactions: List<CoinTransaction>  ?= null,
)
