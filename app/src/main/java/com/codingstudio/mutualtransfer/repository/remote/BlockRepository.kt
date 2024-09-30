package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class BlockRepository {

    suspend fun getAllBlocks() = RetrofitInstance.blockAPI.getAllBlocks()

    suspend fun getBlocksByDistrict(
        district_id: String,
    ) = RetrofitInstance.blockAPI.getBlocksByDistrict(
        district_id = district_id
    )

    suspend fun getBlocksByDistrictAndBlockName(
        block_name: String,
        district_id: String,
    ) = RetrofitInstance.blockAPI.getBlocksByDistrictAndBlockName(
        block_name = block_name,
        district_id = district_id,
    )

    suspend fun getBlocksByState(
        state_id: String,
    ) = RetrofitInstance.blockAPI.getBlocksByState(
        state_id = state_id,
    )



}