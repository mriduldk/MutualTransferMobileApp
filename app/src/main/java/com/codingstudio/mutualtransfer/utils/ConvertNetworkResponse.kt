package com.codingstudio.mutualtransfer.utils

import com.codingstudio.mutualtransfer.model.Resource
import retrofit2.Response

class ConvertNetworkResponse {

    fun <T> process(response: Response<T>): Resource<T> {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


}