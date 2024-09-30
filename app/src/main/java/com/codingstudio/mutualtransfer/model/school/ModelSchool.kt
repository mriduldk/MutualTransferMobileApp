package com.codingstudio.mutualtransfer.model.school

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(tableName = "school")
@Parcelize
data class ModelSchool (
    @PrimaryKey
    val schoolId : String = UUID.randomUUID().toString(),
    val schoolName : String ?= null,

    val schoolBlockId : String ?= null,
    val schoolBlockName : String ?= null,

    val schoolDistrictId : String ?= null,
    val schoolDistrictName : String ?= null,

    val schoolStateName : String ?= null,
    val schoolStateId : String ?= null,

    val schoolCreatedOn : String ?= null,
    val schoolCreatedBy : String ?= null,

    val isDeleted : Boolean = false

) : Parcelable
