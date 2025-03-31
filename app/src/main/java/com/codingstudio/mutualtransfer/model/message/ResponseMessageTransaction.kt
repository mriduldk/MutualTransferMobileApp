package com.codingstudio.mutualtransfer.model.message

import android.os.Message

data class ResponseMessageTransaction (
    val status : Int,
    val message: String,
    val MessageContent: List<ModelMessageTransactions> ?= null
)