package com.codingstudio.mutualtransfer.model.auth

data class ResponseUserDetails (
    val status : Int,
    val message: String,
    val userDetails: UserDetails ?= null
)