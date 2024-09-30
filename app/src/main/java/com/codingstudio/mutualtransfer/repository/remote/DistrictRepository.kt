package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class DistrictRepository {

    suspend fun getAllDistricts() = RetrofitInstance.districtAPI.getAllDistricts()

    suspend fun getDistrictByName(
        district_name: String,
    ) = RetrofitInstance.districtAPI.getDistrictByName(
        district_name = district_name
    )

    suspend fun getDistrictsByStateAndDistrictName(
        district_name: String,
        state_id: String,
    ) = RetrofitInstance.districtAPI.getDistrictsByStateAndDistrictName(
        district_name = district_name,
        state_id = state_id,
    )

    suspend fun getDistrictByState(
        state_id: String,
    ) = RetrofitInstance.districtAPI.getDistrictByState(
        state_id = state_id,
    )



}