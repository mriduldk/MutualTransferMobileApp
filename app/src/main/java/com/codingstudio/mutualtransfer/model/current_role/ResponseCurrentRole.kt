package com.codingstudio.mutualtransfer.model.current_role

import com.codingstudio.mutualtransfer.model.search.ModelSearchResult
import com.codingstudio.mutualtransfer.model.search.SearchedType

data class ResponseCurrentRole (
    val status : Int,
    val message: String,
    val roles: List<ModelCurrentRole> ?= null,
) {
    fun toSearchResults() : List<ModelSearchResult>? {
        return roles?.map { role ->
            ModelSearchResult(
                searchResultId = role.current_role_id,
                searchResultText = role.current_role_name,
                searchedType = SearchedType.CURRENT_ROLE
            )
        }
    }
}