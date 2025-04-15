package com.codingstudio.mutualtransfer.model.message

data class ResponseMessageTransaction (
    val status : Int,
    val message: String,
    val MessageContent: List<ModelMessageTransactions> ?= null,
    val Message: ModelMessage ?= null
)