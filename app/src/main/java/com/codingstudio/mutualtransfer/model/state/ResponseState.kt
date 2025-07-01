package com.codingstudio.mutualtransfer.model.state

import com.codingstudio.mutualtransfer.model.search.ModelSearchResult
import com.codingstudio.mutualtransfer.model.search.SearchedType

data class ResponseState (
    val status : Int,
    val message: String,
    val state: ModelState ?= null,
    val states: List<ModelState> ?= null,
) {
    fun toSearchResults() : List<ModelSearchResult>? {
        return states?.map { state ->
            ModelSearchResult(
                searchResultId = state.state_id,
                searchResultText = state.state_name,
                searchedType = SearchedType.STATE
            )
        }
    }
}