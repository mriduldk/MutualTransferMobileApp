package com.codingstudio.mutualtransfer.model.search

data class ResponseSearchResultNew (
    val status : Int,
    val message: String,
    val searchResult: List<ModelSearchResultOfPersonNew> ?= null
)