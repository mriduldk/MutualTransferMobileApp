package com.codingstudio.mutualtransfer.utils

import androidx.lifecycle.MutableLiveData
import com.codingstudio.mutualtransfer.model.Resource
import retrofit2.Response
import java.io.IOException

sealed class HandleNetworkResponse<T>(
    val mutableLiveData: MutableLiveData<EventWrapper<Resource<T>>>,
    val response: Response<T>
) {
    class Check<T>(
        response: Response<T>,
        mutableLiveData: MutableLiveData<EventWrapper<Resource<T>>>
    ): HandleNetworkResponse<T>(response = response, mutableLiveData = mutableLiveData) {

        fun process() {

            try {
                when {

                    response.code() == Constants.STATUS_SUCCESS -> {
                        mutableLiveData.postValue(
                            EventWrapper(
                                Resource.Success(response.body()!!)
                            )
                        )
                    }

                    response.code() == Constants.STATUS_NOT_FOUND -> {
                        mutableLiveData.postValue(
                            EventWrapper(
                                Resource.Error(Constants.NOT_FOUND)
                            )
                        )
                    }

                    response.code() == Constants.STATUS_INTERNAL_ERROR -> {
                        mutableLiveData.postValue(
                            EventWrapper(
                                Resource.Error(Constants.SERVER_ERROR)
                            )
                        )
                    }

                    response.code() == Constants.STATUS_CONFLICT -> {
                        mutableLiveData.postValue(
                            EventWrapper(
                                Resource.Error(Constants.CONFLICT)
                            )
                        )
                    }

                    else -> {
                        mutableLiveData.postValue(
                            EventWrapper(
                                ConvertNetworkResponse().process(response)
                            )
                        )
                    }
                }
            }
            catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> mutableLiveData.postValue(EventWrapper(Resource.Error(Constants.NETWORK_FAILURE)))
                    else -> mutableLiveData.postValue(EventWrapper(Resource.Error(Constants.CONVERSION_ERROR)))
                }
            }

        }

    }
}