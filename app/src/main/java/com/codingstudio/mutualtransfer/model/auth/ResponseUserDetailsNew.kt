package com.codingstudio.mutualtransfer.model.auth

data class ResponseUserDetailsNew (
    val status : Int,
    val message: String,
    val userDetailsNew: UserDetailsNew ?= null
)