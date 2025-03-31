package com.codingstudio.mutualtransfer.ui.message.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingstudio.mutualtransfer.model.Resource
import com.codingstudio.mutualtransfer.model.message.ResponseMessage
import com.codingstudio.mutualtransfer.model.message.ResponseMessageTransaction
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewed
import com.codingstudio.mutualtransfer.model.search.ResponseSearchResult
import com.codingstudio.mutualtransfer.model.search.ResponseSearchedPerson
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedRepository
import com.codingstudio.mutualtransfer.repository.remote.MessageRepository
import com.codingstudio.mutualtransfer.utils.Constants
import com.codingstudio.mutualtransfer.utils.EventWrapper
import com.codingstudio.mutualtransfer.utils.HandleNetworkResponse
import com.codingstudio.mutualtransfer.utils.HasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val application: Application,
    private val messageRepository: MessageRepository
) : ViewModel() {


    private val _storeMessage: MutableLiveData<EventWrapper<Resource<ResponseMessage>>> =
        MutableLiveData()
    val storeMessage: LiveData<EventWrapper<Resource<ResponseMessage>>>
        get() = _storeMessage

    fun storeMessageFun(
        sender_id: String,
        receiver_id: String,
        last_message_content: String,
    ) = viewModelScope.launch {

        _storeMessage.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = messageRepository.storeMessage(
                sender_id = sender_id,
                receiver_id = receiver_id,
                last_message_content = last_message_content,
            )
            HandleNetworkResponse.Check(response, _storeMessage).process()
        } else {
            _storeMessage.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _acceptMessage: MutableLiveData<EventWrapper<Resource<ResponseMessage>>> =
        MutableLiveData()
    val acceptMessage: LiveData<EventWrapper<Resource<ResponseMessage>>>
        get() = _acceptMessage

    fun acceptMessageFun(
        message_id: String,
        status: String,
        user_id: String,
    ) = viewModelScope.launch {

        _acceptMessage.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = messageRepository.acceptMessage(
                message_id = message_id,
                status = status,
                user_id = user_id,
            )
            HandleNetworkResponse.Check(response, _acceptMessage).process()
        } else {
            _acceptMessage.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _getMessagesByUserId: MutableLiveData<EventWrapper<Resource<ResponseMessage>>> =
        MutableLiveData()
    val getMessagesByUserId: LiveData<EventWrapper<Resource<ResponseMessage>>>
        get() = _getMessagesByUserId

    fun getMessagesByUserIdFun(
        userId: String,
    ) = viewModelScope.launch {

        _getMessagesByUserId.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = messageRepository.getMessagesByUserId(
                userId = userId,
            )
            HandleNetworkResponse.Check(response, _getMessagesByUserId).process()
        } else {
            _getMessagesByUserId.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _storeMessageTransactions: MutableLiveData<EventWrapper<Resource<ResponseMessageTransaction>>> =
        MutableLiveData()
    val storeMessageTransactions: LiveData<EventWrapper<Resource<ResponseMessageTransaction>>>
        get() = _storeMessageTransactions

    fun storeMessageTransactionsFun(
        message_id: String,
        message_content: String,
        sender_id: String,
        receiver_id: String,
    ) = viewModelScope.launch {

        _storeMessageTransactions.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = messageRepository.storeMessageTransactions(
                message_id = message_id,
                message_content = message_content,
                sender_id = sender_id,
                receiver_id = receiver_id,
            )
            HandleNetworkResponse.Check(response, _storeMessageTransactions).process()
        } else {
            _storeMessageTransactions.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }



    private val _getMessageTransactionsByMessageId: MutableLiveData<EventWrapper<Resource<ResponseMessageTransaction>>> =
        MutableLiveData()
    val getMessageTransactionsByMessageId: LiveData<EventWrapper<Resource<ResponseMessageTransaction>>>
        get() = _getMessageTransactionsByMessageId

    fun getMessageTransactionsByMessageIdFun(
        message_id: String,
        user_id: String,
    ) = viewModelScope.launch {

        _getMessageTransactionsByMessageId.postValue(EventWrapper(Resource.Loading()))

        if (HasInternetConnection().check(application)) {

            val response = messageRepository.getMessageTransactionsByMessageId(
                message_id = message_id,
                user_id = user_id,
            )
            HandleNetworkResponse.Check(response, _getMessageTransactionsByMessageId).process()
        } else {
            _getMessageTransactionsByMessageId.postValue(EventWrapper(Resource.Error(Constants.NO_INTERNET)))
        }

    }




}