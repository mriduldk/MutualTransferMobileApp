package com.codingstudio.mutualtransfer.model.district

import com.codingstudio.mutualtransfer.model.search.ModelSearchResult
import com.codingstudio.mutualtransfer.model.search.SearchedType

data class ResponseDistrict (
    val status : Int,
    val message: String,
    val district: ModelDistrict ?= null,
    val districts: List<ModelDistrict> ?= null,
) {
    fun toSearchResults() : List<ModelSearchResult>? {
        return districts?.map { district ->
            ModelSearchResult(
                searchResultId = district.district_id,
                searchResultText = district.district_name,
                searchedType = SearchedType.DISTRICT
            )
        }
    }
}