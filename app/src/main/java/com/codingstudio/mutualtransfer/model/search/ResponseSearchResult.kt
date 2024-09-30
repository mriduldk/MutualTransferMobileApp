package com.codingstudio.mutualtransfer.model.search

data class ResponseSearchResult (
    val status : Int,
    val message: String,
    val searchResult: List<ModelSearchResultOfPerson> ?= null
)