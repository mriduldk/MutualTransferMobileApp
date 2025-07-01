package com.codingstudio.mutualtransfer.model.current_role

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(tableName = "district")
@Parcelize
data class ModelCurrentRole (
    @PrimaryKey
    val current_role_id: String,
    val current_role_name: String? = null,
    val user_type: String? = null,

    val created_on: String? = null,
    val created_by: String? = null,
    val modified_on: String? = null,
    val modified_by: String? = null,
    val is_delete: Int = 0,
    val created_at: String? = null,
    val updated_at: String? = null ,
) : Parcelable
