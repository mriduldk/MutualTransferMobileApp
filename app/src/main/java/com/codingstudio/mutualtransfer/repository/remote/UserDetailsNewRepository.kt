package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class UserDetailsNewRepository {

    suspend fun saveUserPersonalInformation(
        name: String,
        user_id: String,
        email: String,
        gender: String,
        user_type: String,
        user_type_id: String
    ) = RetrofitInstance.userDetailsNewAPI.saveUserPersonalInformation(
        name = name,
        user_id = user_id,
        email = email,
        gender = gender,
        user_type = user_type,
        user_type_id = user_type_id
    )


    suspend fun saveUserJobDetails(
        user_id: String,
        current_role: String,
        employee_code: String,
        department: String,
        zone_division: String,
        service_type: String,
        current_organisation_name: String,
        job_address_village: String,
        job_address_district: String,
        job_address_block: String,
        job_address_state: String,
        job_address_pin: String
    ) = RetrofitInstance.userDetailsNewAPI.saveUserJobDetails(
        user_id = user_id,
        current_role = current_role,
        employee_code = employee_code,
        department = department,
        zone_division = zone_division,
        service_type = service_type,
        current_organisation_name = current_organisation_name,
        job_address_village = job_address_village,
        job_address_district = job_address_district,
        job_address_block = job_address_block,
        job_address_state = job_address_state,
        job_address_pin = job_address_pin
    )


    suspend fun saveUserPreferredDistrict(
        preferred_district_1: String,
        preferred_district_2: String,
        preferred_district_3: String,
        user_id: String
    ) = RetrofitInstance.userDetailsNewAPI.saveUserPreferredDistrict(
        preferred_district_1 = preferred_district_1,
        preferred_district_2 = preferred_district_2,
        preferred_district_3 = preferred_district_3,
        user_id = user_id
    )


    suspend fun changeActivelyLookingStatus(
        is_actively_looking: Int,
        user_id: String,
    ) = RetrofitInstance.userDetailsNewAPI.changeActivelyLookingStatus(
        is_actively_looking = is_actively_looking,
        user_id = user_id
    )


    suspend fun getUserDetailsById(
        user_phone: String,
        user_id: String,
    ) = RetrofitInstance.userDetailsNewAPI.getUserDetailsById(
        user_phone = user_phone,
        user_id = user_id
    )


}