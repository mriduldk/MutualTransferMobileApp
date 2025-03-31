package com.codingstudio.mutualtransfer.api

import com.codingstudio.mutualtransfer.model.auth.ResponseLogin
import com.codingstudio.mutualtransfer.model.block.ResponseBlock
import com.codingstudio.mutualtransfer.model.district.ResponseDistrict
import com.codingstudio.mutualtransfer.model.message.ResponseMessage
import com.codingstudio.mutualtransfer.model.message.ResponseMessageTransaction
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitMessageAPI {

    @POST("message/messages")
    @FormUrlEncoded
    suspend fun storeMessage(
        @Field("sender_id")
        sender_id : String,
        @Field("receiver_id")
        receiver_id : String,
        @Field("last_message_content")
        last_message_content : String,
    ): Response<ResponseMessage>


    @POST("message/acceptMessage")
    @FormUrlEncoded
    suspend fun acceptMessage(
        @Field("message_id")
        message_id : String,
        @Field("status")
        status : String,
        @Field("user_id")
        user_id : String,
    ): Response<ResponseMessage>


    @POST("message/getMessagesByUserId")
    @FormUrlEncoded
    suspend fun getMessagesByUserId(
        @Field("user_id")
        userId : String,
    ): Response<ResponseMessage>



    @POST("message/messageTransactions")
    @FormUrlEncoded
    suspend fun storeMessageTransactions(
        @Field("message_id")
        message_id : String,
        @Field("message_content")
        message_content : String,
        @Field("sender_id")
        sender_id : String,
        @Field("receiver_id")
        receiver_id : String,
    ): Response<ResponseMessageTransaction>



    @POST("message/getMessageTransactionsByMessageId")
    @FormUrlEncoded
    suspend fun getMessageTransactionsByMessageId(
        @Field("message_id")
        message_id : String,
        @Field("user_id")
        user_id : String
    ): Response<ResponseMessageTransaction>


}