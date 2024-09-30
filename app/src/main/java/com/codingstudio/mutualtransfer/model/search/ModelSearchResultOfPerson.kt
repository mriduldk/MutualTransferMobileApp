package com.codingstudio.mutualtransfer.model.search

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ModelSearchResultOfPerson (
    val id: String? = null,
    val user_details_id: String? = null,
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

) : Parcelable {

    fun toRecentlyViewedModel(): ModelRecentlyViewed {

        return ModelRecentlyViewed(
            user_details_id = this.user_details_id ?: UUID.randomUUID().toString(),
            fk_user_id = this.fk_user_id,
            name = this.name,
            gender = this.gender,
            email = this.email,
            phone = this.phone,
            employee_code = this.employee_code,
            school_type = this.school_type,
            teacher_type = this.teacher_type,
            subject_type = this.subject_type,
            school_name = this.school_name,
            udice_code = this.udice_code,
            school_address_vill = this.school_address_vill,
            school_address_district = this.school_address_district,
            school_address_block = this.school_address_block,
            school_address_state = this.school_address_state,
            school_address_pin = this.school_address_pin,
            created_on = this.created_on,
            created_by = this.created_by,
            modified_on = this.modified_on,
            modified_by = this.modified_by,
            is_actively_looking = this.is_actively_looking,
            created_at = this.created_at,
            updated_at = this.updated_at,
            payment_id = this.payment_id,
            payment_done_by = this.payment_done_by,
            payment_done_for = this.payment_done_for,
            amount = this.amount,
            is_paid = this.is_paid,
            pay_to_view_amount = this.pay_to_view_amount,

            preferred_district_1 = this.preferred_district_1,
            preferred_district_2 = this.preferred_district_2,
            preferred_district_3 = this.preferred_district_3,
            my_referral_code = this.my_referral_code,
            is_referral_code_used = this.is_referral_code_used,
            used_referral_code = this.used_referral_code,
            is_mutually_transferred = this.is_mutually_transferred,
            transfer_remarks = this.transfer_remarks,
            district_match_flag = this.district_match_flag,
            amalgamation = this.amalgamation,
        )
    }
}

