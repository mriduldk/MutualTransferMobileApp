package com.codingstudio.mutualtransfer.model.block

import com.codingstudio.mutualtransfer.model.search.ModelSearchResult
import com.codingstudio.mutualtransfer.model.search.SearchedType

data class ResponseBlock (
    val status : Int,
    val message: String,
    val block: ModelBlock ?= null,
    val blocks: List<ModelBlock> ?= null,
){
    fun toSearchResults() : List<ModelSearchResult>? {
        return blocks?.map { block ->
            ModelSearchResult(
                searchResultId = block.block_id,
                searchResultText = block.block_name,
                searchedType = SearchedType.BLOCK
            )
        }
    }
}