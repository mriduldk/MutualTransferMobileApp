package com.codingstudio.mutualtransfer.model.subject

data class ResponseSubject (
    val status : Int,
    val message: String,
    val subjects: List<Subject> ?= null,
)