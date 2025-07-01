package com.codingstudio.mutualtransfer.model.user_type

data class UserType (
    val id: Int,
    val user_type_id: String,
    val user_type_name: String,
    val user_type_icon: String,
    val user_type_preference: Int,
    val is_delete: Int = 0,
    val created_at: String ?= null,
    val updated_at: String ?= null
)