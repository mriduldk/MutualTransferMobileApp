package com.codingstudio.mutualtransfer.model.wallet

data class WalletOffers(
    val id: Int? = null,
    val wallet_offer_id: String? = null,
    val total_amount: Int? = null,
    val total_coin: Int? = null,
    val discount: Int? = null,
    val message: String? = null,
    val is_new: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)
