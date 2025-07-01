package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance
import retrofit2.http.Field

class SearchRepository {

    suspend fun searchPerson(
        school_address_state: String,
        school_address_district: String,
        user_id: String,
        school_address_block: String,
        school_address_vill: String,
        school_name: String,
    ) = RetrofitInstance.searchAPI.searchPerson(
        school_address_state = school_address_state,
        school_address_district = school_address_district,
        user_id = user_id,
        school_address_block = school_address_block,
        school_address_vill = school_address_vill,
        school_name = school_name
    )


    suspend fun searchPerson_v3(
        job_address_state: String,
        job_address_district: String,
        job_address_block: String,
        department: String,
        current_role: String,
        user_id: String,
    ) = RetrofitInstance.searchAPI.searchPerson_v3(
        job_address_state = job_address_state,
        job_address_district = job_address_district,
        job_address_block = job_address_block,
        department = department,
        current_role = current_role,
        user_id = user_id
    )


    suspend fun viewPersonDetails(
        person_user_id: String,
        user_id: String,
    ) = RetrofitInstance.searchAPI.viewPersonDetails(
        person_user_id = person_user_id,
        user_id = user_id,
    )


    suspend fun viewPersonDetails_v3(
        person_user_id: String,
        user_id: String,
    ) = RetrofitInstance.searchAPI.viewPersonDetails_v3(
        person_user_id = person_user_id,
        user_id = user_id,
    )



}