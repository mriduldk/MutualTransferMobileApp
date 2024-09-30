package com.codingstudio.mutualtransfer.model.wallet

data class ResponseWallet (
    val status : Int,
    val message: String,
    val wallet: Wallet ?= null
)