package com.codingstudio.mutualtransfer.model.search

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(tableName = "recently_viewed")
@Parcelize
data class ModelRecentlyViewed (
    @PrimaryKey
    val user_details_id: String,
    val fk_user_id: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val employee_code: String? = null,
    val school_type: String? = null,
    val teacher_type: String? = null,
    val subject_type: String? = null,
    val school_name: String? = null,
    val udice_code: String? = null,
    val school_address_vill: String? = null,
    val school_address_district: String? = null,
    val school_address_block: String? = null,
    val school_address_state: String? = null,
    val school_address_pin: String? = null,
    val created_on: String? = null,
    val created_by: String? = null,
    val modified_on: String? = null,
    val modified_by: String? = null,
    val is_actively_looking: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val payment_id: String? = null,
    val payment_done_by: String? = null,
    val payment_done_for: String? = null,
    val amount: String? = null,

    val is_paid: Int = 0,
    val pay_to_view_amount: String? = null,

    val preferred_district_1: String? = null,
    val preferred_district_2: String? = null,
    val preferred_district_3: String? = null,
    val my_referral_code: String? = null,
    val is_referral_code_used: Int = 0,
    val used_referral_code: String? = null,

    val is_mutually_transferred: Int = 0,
    val transfer_remarks: String? = null,
    val district_match_flag: Int = 0,
    val amalgamation: Int = 0,

    val created_on_local: Long = System.currentTimeMillis()

    ) : Parcelable

