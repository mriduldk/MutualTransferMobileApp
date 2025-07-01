package com.codingstudio.mutualtransfer.model.message

data class ResponseMessage (
    val status : Int,
    val message: String,
    val Message: ModelMessage ?= null,
    val Messages: List<ModelMessage> ?= null
)