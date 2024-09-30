package com.codingstudio.mutualtransfer.model.wallet

data class ResponseWalletOffers (
    val status : Int,
    val message: String,
    val walletOffers: List<WalletOffers> ?= null
)