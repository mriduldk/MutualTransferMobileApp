package com.codingstudio.mutualtransfer.model.zone_division

import com.codingstudio.mutualtransfer.model.search.ModelSearchResult
import com.codingstudio.mutualtransfer.model.search.SearchedType

data class ResponseZoneDivision (
    val status : Int,
    val message: String,
    val zoneDivisions: List<ModelZoneDivision> ?= null,
) {
    fun toSearchResults() : List<ModelSearchResult>? {
        return zoneDivisions?.map { zoneDivision ->
            ModelSearchResult(
                searchResultId = zoneDivision.zone_division_id,
                searchResultText = zoneDivision.zone_division_name,
                searchedType = SearchedType.ZONE_DIVISION
            )
        }
    }
}