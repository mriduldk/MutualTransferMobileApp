package com.codingstudio.mutualtransfer.model.search

data class ResponseSearchedPerson (
    val status : Int,
    val message: String,
    val personDetails: ModelSearchResultOfPerson ?= null
)