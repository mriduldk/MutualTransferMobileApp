package com.codingstudio.mutualtransfer.model.state

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "state")
@Parcelize
data class ModelState (
    @PrimaryKey
    val state_id: String,
    val state_name: String? = null,
    val created_on: String? = null,
    val created_by: String? = null,
    val modified_on: String? = null,
    val modified_by: String? = null,
    val is_delete: Int = 0,
    val created_at: String? = null,
    val updated_at: String? = null ,
) : Parcelable
