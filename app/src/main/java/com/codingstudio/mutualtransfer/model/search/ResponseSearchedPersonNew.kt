package com.codingstudio.mutualtransfer.model.search

data class ResponseSearchedPersonNew (
    val status : Int,
    val message: String,
    val personDetails: ModelSearchResultOfPersonNew ?= null
)