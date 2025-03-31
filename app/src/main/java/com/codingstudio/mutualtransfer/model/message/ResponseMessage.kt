package com.codingstudio.mutualtransfer.model.message

import android.os.Message

data class ResponseMessage (
    val status : Int,
    val message: String,
    val MessageContent: List<ModelMessage> ?= null
)