package com.codingstudio.mutualtransfer.model.auth

data class User(
    val id: Int? = null,
    val user_id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val is_email_verified: Int? = null,
    val email_verified_at: String? = null,
    val phone: String? = null,
    val otp: String? = null,
    val otp_valid_upto: String? = null,
    val fcm_token: String? = null,
    val access_token: String? = null,
    val is_active: Int? = null,
    val is_delete: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)
