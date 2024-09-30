package com.codingstudio.mutualtransfer.model.search

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ModelSearchResult (
    val searchResultId : String ?= null,
    val searchResultText : String ?= null,
    val searchedType : SearchedType?= null,
) : Parcelable
