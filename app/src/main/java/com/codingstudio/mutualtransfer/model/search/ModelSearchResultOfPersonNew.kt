package com.codingstudio.mutualtransfer.model.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ModelSearchResultOfPersonNew (
    val id: String? = null,
    val user_details_new_id: String? = null,
    val fk_user_id: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val email: String? = null,
    val phone: String? = null,

    val user_type: String? = null,
    val user_type_id: String? = null,
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

    val created_on: String? = null,
    val created_by: String? = null,
    val modified_on: String? = null,
    val modified_by: String? = null,
    val is_actively_looking: Int? = null,

    val preferred_district_1: String? = null,
    val preferred_district_2: String? = null,
    val preferred_district_3: String? = null,

    val is_mutually_transferred: Int = 0,
    val transfer_remarks: String? = null,
    val district_match_flag: Int = 0,

) : Parcelable {

    fun toRecentlyViewedModelNew(): ModelRecentlyViewedNew {

        return ModelRecentlyViewedNew(
            user_details_new_id = this.user_details_new_id ?: UUID.randomUUID().toString(),
            fk_user_id = this.fk_user_id,
            name = this.name,
            gender = this.gender,
            email = this.email,
            phone = this.phone,

            user_type = this.user_type,
            user_type_id = this.user_type_id,
            current_role = this.current_role,
            employee_code = this.employee_code,
            department = this.department,
            zone_division = this.zone_division,
            service_type = this.service_type,
            current_organisation_name = this.current_organisation_name,
            extra = this.extra,
            job_address_village = this.job_address_village,
            job_address_district = this.job_address_district,
            job_address_block = this.job_address_block,
            job_address_state = this.job_address_state,
            job_address_pin = this.job_address_pin,

            created_on = this.created_on,
            created_by = this.created_by,
            modified_on = this.modified_on,
            modified_by = this.modified_by,
            is_actively_looking = this.is_actively_looking,

            preferred_district_1 = this.preferred_district_1,
            preferred_district_2 = this.preferred_district_2,
            preferred_district_3 = this.preferred_district_3,
            is_mutually_transferred = this.is_mutually_transferred,
            transfer_remarks = this.transfer_remarks,
            district_match_flag = this.district_match_flag,
        )
    }
}

