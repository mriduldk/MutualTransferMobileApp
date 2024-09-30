package com.codingstudio.mutualtransfer.model.district

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(tableName = "district")
@Parcelize
data class ModelDistrict (
    @PrimaryKey
    val district_id: String,
    val district_name: String? = null,
    val state_name: String? = null,
    val state_id: String? = null,
    val created_on: String? = null,
    val created_by: String? = null,
    val modified_on: String? = null,
    val modified_by: String? = null,
    val is_delete: Int = 0,
    val created_at: String? = null,
    val updated_at: String? = null ,
) : Parcelable
