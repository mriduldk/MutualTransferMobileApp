package com.codingstudio.mutualtransfer.model.department

import com.codingstudio.mutualtransfer.model.search.ModelSearchResult
import com.codingstudio.mutualtransfer.model.search.SearchedType

data class ResponseDepartment (
    val status : Int,
    val message: String,
    val departments: List<ModelDepartment> ?= null,
) {
    fun toSearchResults() : List<ModelSearchResult>? {
        return departments?.map { department ->
            ModelSearchResult(
                searchResultId = department.department_id,
                searchResultText = department.department_name,
                searchedType = SearchedType.DEPARTMENT
            )
        }
    }
}