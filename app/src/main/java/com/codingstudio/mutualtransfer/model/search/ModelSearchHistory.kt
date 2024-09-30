package com.codingstudio.mutualtransfer.model.search

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(tableName = "search_history")
@Parcelize
data class ModelSearchHistory (
    @PrimaryKey
    val searchHistoryId : String = UUID.randomUUID().toString(),
    val searchedText : String = "",
    val searchedTimeInLong : Long = System.currentTimeMillis(),
    val searchedTimeInString : String ?= null,

    val searchedType : SearchedType?= null,
    val searchedTypeId : String ?= null,

    val isDeleted : Boolean = false
) : Parcelable