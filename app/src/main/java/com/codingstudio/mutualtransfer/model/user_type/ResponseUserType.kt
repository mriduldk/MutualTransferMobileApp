package com.codingstudio.mutualtransfer.model.user_type

data class ResponseUserType (
    val status : Int,
    val message: String,
    val userTypes: List<UserType> ?= null,
)