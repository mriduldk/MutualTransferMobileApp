package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class StateRepository {

    suspend fun getAllStates() = RetrofitInstance.stateAPI.getAllStates()

    suspend fun getStateByName(
        state_name: String,
    ) = RetrofitInstance.stateAPI.getStateByName(
        state_name = state_name
    )


}