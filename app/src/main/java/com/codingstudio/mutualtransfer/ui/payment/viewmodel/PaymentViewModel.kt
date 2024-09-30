package com.codingstudio.mutualtransfer.ui.payment.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.online_payment.ResponseOnlinePayment
import com.codingstudio.mutualtransfer.model.payment.ResponsePayment
import com.codingstudio.mutualtransfer.model.payment.ResponsePaymentHistory
import com.codingstudio.mutualtransfer.repository.remote.PaymentRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val application: Application,
    private val paymentRepository: PaymentRepository
) : ViewModel() {


    private val _saveUserPayForAnotherUserObserver: MutableLiveData<EventWrapper<Resource<ResponsePayment>>> =
        MutableLiveData()
    val saveUserPayForAnotherUserObserver: LiveData<EventWrapper<Resource<ResponsePayment>>>
        get() = _saveUserPayForAnotherUserObserver

    fun saveUserPayForAnotherUserFun(
        payment_done_by: String,
        payment_done_for: String,
        amount: String,
    ) = viewModelScope.launch {

        _saveUserPayForAnotherUserObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = paymentRepository.saveUserPayForAnotherUser(
                payment_done_by = payment_done_by,
                payment_done_for = payment_done_for,
                amount = amount
            )
            HandleNetworkResponse.Check(response, _saveUserPayForAnotherUserObserver).process()
        } else {
            _saveUserPayForAnotherUserObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _savePaymentHistoryObserver: MutableLiveData<EventWrapper<Resource<ResponsePaymentHistory>>> =
        MutableLiveData()
    val savePaymentHistoryObserver: LiveData<EventWrapper<Resource<ResponsePaymentHistory>>>
        get() = _savePaymentHistoryObserver

    fun savePaymentHistoryFun(
        paid_amount: Int,
        status: String,
        user_id: String,
    ) = viewModelScope.launch {

        _savePaymentHistoryObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = paymentRepository.savePaymentHistory(
                paid_amount = paid_amount,
                status = status,
                user_id = user_id,
            )
            HandleNetworkResponse.Check(response, _savePaymentHistoryObserver).process()
        } else {
            _savePaymentHistoryObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _createRazorPayOrderObserver: MutableLiveData<EventWrapper<Resource<ResponseOnlinePayment>>> =
        MutableLiveData()
    val createRazorPayOrderObserver: LiveData<EventWrapper<Resource<ResponseOnlinePayment>>>
        get() = _createRazorPayOrderObserver

    fun createRazorPayOrderFun(
        user_id: String,
        coins: String,
        amount: String,
    ) = viewModelScope.launch {

        _createRazorPayOrderObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = paymentRepository.createRazorPayOrder(
                user_id = user_id,
                coins = coins,
                amount = amount,
            )
            HandleNetworkResponse.Check(response, _createRazorPayOrderObserver).process()
        } else {
            _createRazorPayOrderObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _verifyRazorPayPaymentObserver: MutableLiveData<EventWrapper<Resource<ResponseOnlinePayment>>> =
        MutableLiveData()
    val verifyRazorPayPaymentObserver: LiveData<EventWrapper<Resource<ResponseOnlinePayment>>>
        get() = _verifyRazorPayPaymentObserver

    fun verifyRazorPayPaymentFun(
        user_id: String,
        online_payment_id: String,
        razorpay_signature: String,
        razorpay_order_id: String,
        razorpay_payment_id: String,
    ) = viewModelScope.launch {

        _verifyRazorPayPaymentObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = paymentRepository.verifyRazorPayPayment(
                user_id = user_id,
                online_payment_id = online_payment_id,
                razorpay_signature = razorpay_signature,
                razorpay_order_id = razorpay_order_id,
                razorpay_payment_id = razorpay_payment_id,
            )
            HandleNetworkResponse.Check(response, _verifyRazorPayPaymentObserver).process()
        } else {
            _verifyRazorPayPaymentObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _refreshPaymentStatusWithOrderIdObserver: MutableLiveData<EventWrapper<Resource<ResponseOnlinePayment>>> =
        MutableLiveData()
    val refreshPaymentStatusWithOrderIdObserver: LiveData<EventWrapper<Resource<ResponseOnlinePayment>>>
        get() = _refreshPaymentStatusWithOrderIdObserver

    fun refreshPaymentStatusWithOrderIdFun(
        user_id: String,
        online_payment_id: String,
        razorpay_order_id: String,
    ) = viewModelScope.launch {

        _refreshPaymentStatusWithOrderIdObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = paymentRepository.refreshPaymentStatusWithOrderId(
                user_id = user_id,
                online_payment_id = online_payment_id,
                razorpay_order_id = razorpay_order_id,
            )
            HandleNetworkResponse.Check(response, _refreshPaymentStatusWithOrderIdObserver).process()
        } else {
            _refreshPaymentStatusWithOrderIdObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _getUsersPaymentDetailsObserver: MutableLiveData<EventWrapper<Resource<ResponseOnlinePayment>>> =
        MutableLiveData()
    val getUsersPaymentDetailsObserver: LiveData<EventWrapper<Resource<ResponseOnlinePayment>>>
        get() = _getUsersPaymentDetailsObserver

    fun getUsersPaymentDetailsFun(
        user_id: String,
    ) = viewModelScope.launch {

        _getUsersPaymentDetailsObserver.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = paymentRepository.getUsersPaymentDetails(
                user_id = user_id,
            )
            HandleNetworkResponse.Check(response, _getUsersPaymentDetailsObserver).process()
        } else {
            _getUsersPaymentDetailsObserver.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }






}