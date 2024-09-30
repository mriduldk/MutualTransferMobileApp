package com.codingstudio.mutualtransfer.model.coin_transactions


data class CoinTransaction (
    val id: Int? = null,
    val coin_amount: Int? = null,
    val transaction_message: String? = null,
    val transaction_done_for: String? = null,
    val transaction_type: String? = null,
    val transaction_category: String? = null,
    val created_on: String? = null,
    val created_by: String? = null,
    val modified_on: String? = null,
    val modified_by: String? = null,
    val is_delete: Int = 0,
    val created_at: String? = null,
    val updated_at: String? = null,
)

enum class EnumTransactionType {
    CREDIT,
    DEBIT
}