package com.codingstudio.mutualtransfer.model.message

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codingstudio.mutualtransfer.model.payment.Payment
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ModelMessage (
    val id: Int,
    val sender_id: String? = null,
    val sender_name: String? = null,
    val receiver_id: String? = null,
    val receiver_name: String? = null,
    val last_message_content: String? = null,
    val last_message_sent_by: String? = null,
    val is_read: Boolean = false,
    val status: String? = null,
    val sent_at: String? = null,
    val is_deleted: Boolean = false,
    val created_at: String? = null,
    val updated_at: String? = null,

) : Parcelable
