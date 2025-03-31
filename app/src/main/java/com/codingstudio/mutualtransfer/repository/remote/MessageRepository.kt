package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class MessageRepository {

    suspend fun storeMessage(
        sender_id: String,
        receiver_id: String,
        last_message_content: String,
    ) = RetrofitInstance.messageAPI.storeMessage(
        sender_id = sender_id,
        receiver_id = receiver_id,
        last_message_content = last_message_content,
    )

    suspend fun acceptMessage(
        message_id: String,
        status: String,
        user_id: String,
    ) = RetrofitInstance.messageAPI.acceptMessage(
        message_id = message_id,
        status = status,
        user_id = user_id,
    )


    suspend fun getMessagesByUserId(
        userId: String,
    ) = RetrofitInstance.messageAPI.getMessagesByUserId(
        userId = userId,
    )


    suspend fun storeMessageTransactions(
        message_id: String,
        message_content: String,
        sender_id: String,
        receiver_id: String,
    ) = RetrofitInstance.messageAPI.storeMessageTransactions(
        message_id = message_id,
        message_content = message_content,
        sender_id = sender_id,
        receiver_id = receiver_id,
    )


    suspend fun getMessageTransactionsByMessageId(
        message_id: String,
        user_id: String,
    ) = RetrofitInstance.messageAPI.getMessageTransactionsByMessageId(
        message_id = message_id,
        user_id = user_id,
    )




}