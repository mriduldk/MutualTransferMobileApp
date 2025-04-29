package com.codingstudio.mutualtransfer.model.auth

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID


@Entity(tableName = "user_details_new")
@Parcelize
data class UserDetailsNew(
    val id: Int? = null,
    @PrimaryKey
    val user_details_new_id: String,
    val fk_user_id: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val email: String? = null,
    val phone: String? = null,

    val user_type: String? = null,
    val current_role: String? = null,
    val employee_code: String? = null,
    val department: String? = null,
    val zone_division: String? = null,
    val service_type: String? = null,
    val current_organisation_name: String? = null,
    val extra: String? = null,
    val job_address_village: String? = null,
    val job_address_district: String? = null,
    val job_address_block: String? = null,
    val job_address_state: String? = null,
    val job_address_pin: String? = null,

    val preferred_district_1: String? = null,
    val preferred_district_2: String? = null,
    val preferred_district_3: String? = null,
    val my_referral_code: String? = null,
    val is_referral_code_used: Int = 0,
    val used_referral_code: String? = null,

    val is_mutually_transferred: Int = 0,
    val transfer_remarks: String? = null,

    val created_on: String? = null,
    val created_by: String? = null,
    val modified_on: String? = null,
    val modified_by: String? = null,
    val is_actively_looking: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null,

    val district_match_flag: Int = 0,

) : Parcelable
