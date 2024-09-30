package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class SearchRepository {

    suspend fun searchPerson(
        school_address_district: String,
        user_id: String,
        school_address_block: String,
        school_address_vill: String,
        school_name: String,
    ) = RetrofitInstance.searchAPI.searchPerson(
        school_address_district = school_address_district,
        user_id = user_id,
        school_address_block = school_address_block,
        school_address_vill = school_address_vill,
        school_name = school_name
    )


    suspend fun viewPersonDetails(
        person_user_id: String,
        user_id: String,
    ) = RetrofitInstance.searchAPI.viewPersonDetails(
        person_user_id = person_user_id,
        user_id = user_id,
    )



}