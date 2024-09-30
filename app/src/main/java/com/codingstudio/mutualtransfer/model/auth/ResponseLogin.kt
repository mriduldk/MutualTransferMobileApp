package com.codingstudio.mutualtransfer.model.auth

data class ResponseLogin (
    val status : Int,
    val message: String,
    val user: User ?= null,
    val userDetails: UserDetails ?= null
)