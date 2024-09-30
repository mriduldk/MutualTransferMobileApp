package com.codingstudio.mutualtransfer.ui.wallet.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.wallet.ResponseWallet
import com.codingstudio.mutualtransfer.model.wallet.ResponseWalletOffers
import com.codingstudio.mutualtransfer.repository.remote.WalletRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val application: Application,
    private val walletRepository: WalletRepository
) : ViewModel() {


    private val _getWalletOffersObserver: MutableLiveData<EventWrapper<Resource<ResponseWalletOffers>>> =
        MutableLiveData()
    val getWalletOffersObserver: LiveData<EventWrapper<Resource<ResponseWalletOffers>>>
        get() = _getWalletOffersObserver

    fun getWalletOffersFun() = viewModelScope.launch {

        _getWalletOffersObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = walletRepository.getWalletOffers()
            HandleNetworkResponse.Check(response, _getWalletOffersObserver).process()
        } else {
            _getWalletOffersObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }


    private val _getWalletDataByUserObserver: MutableLiveData<EventWrapper<Resource<ResponseWallet>>> =
        MutableLiveData()
    val getWalletDataByUserObserver: LiveData<EventWrapper<Resource<ResponseWallet>>>
        get() = _getWalletDataByUserObserver

    fun getWalletDataByUserFun(user_id: String) = viewModelScope.launch {

        _getWalletDataByUserObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = walletRepository.getWalletDataByUser(user_id = user_id)
            HandleNetworkResponse.Check(response, _getWalletDataByUserObserver).process()
        } else {
            _getWalletDataByUserObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }


}