package com.codingstudio.mutualtransfer.model.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelMessageTransactions (
    val id: Int,
    val message_id: Int,
    val message_content: String? = null,
    val sender_id: String? = null,
    val receiver_id: String? = null,
    val sent_at: String? = null,
    val read_at: String? = null,
    val is_deleted: Boolean = false,
    val created_at: String? = null,
    val updated_at: String? = null
) : Parcelable
