package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class UserDetailsRepository {

    suspend fun saveUserPersonalInformation(
        name: String,
        user_id: String,
        email: String,
        gender: String
    ) = RetrofitInstance.userDetailsAPI.saveUserPersonalInformation(
        name = name,
        user_id = user_id,
        email = email,
        gender = gender
    )


    suspend fun saveUserEmployeeDetails(
        employee_code: String,
        school_type: String,
        teacher_type: String,
        user_id: String,
        subject_type: String
    ) = RetrofitInstance.userDetailsAPI.saveUserEmployeeDetails(
        employee_code = employee_code,
        school_type = school_type,
        teacher_type = teacher_type,
        user_id = user_id,
        subject_type = subject_type
    )


    suspend fun saveUserSchoolDetails(
        school_address_block: String,
        school_address_district: String,
        school_address_pin: String,
        school_address_state: String,
        school_address_vill: String,
        school_name: String,
        udice_code: String,
        user_id: String,
        amalgamation: Int
    ) = RetrofitInstance.userDetailsAPI.saveUserSchoolDetails(
        school_address_block = school_address_block,
        school_address_district = school_address_district,
        school_address_pin = school_address_pin,
        school_address_state = school_address_state,
        school_address_vill = school_address_vill,
        school_name = school_name,
        udice_code = udice_code,
        user_id = user_id,
        amalgamation = amalgamation
    )


    suspend fun saveUserPreferredDistrict(
        preferred_district_1: String,
        preferred_district_2: String,
        preferred_district_3: String,
        user_id: String
    ) = RetrofitInstance.userDetailsAPI.saveUserPreferredDistrict(
        preferred_district_1 = preferred_district_1,
        preferred_district_2 = preferred_district_2,
        preferred_district_3 = preferred_district_3,
        user_id = user_id
    )


    suspend fun changeActivelyLookingStatus(
        is_actively_looking: Int,
        user_id: String,
    ) = RetrofitInstance.userDetailsAPI.changeActivelyLookingStatus(
        is_actively_looking = is_actively_looking,
        user_id = user_id
    )


    suspend fun useReferralCode(
        referral_code: String,
        user_id: String,
    ) = RetrofitInstance.userDetailsAPI.useReferralCode(
        referral_code = referral_code,
        user_id = user_id
    )


    suspend fun getUserDetailsById(
        user_phone: String,
        user_id: String,
    ) = RetrofitInstance.userDetailsAPI.getUserDetailsById(
        user_phone = user_phone,
        user_id = user_id
    )


}