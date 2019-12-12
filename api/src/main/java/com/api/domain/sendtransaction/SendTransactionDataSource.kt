package com.api.domain.sendtransaction

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SendTransactionDataSource {
    @FormUrlEncoded
    @POST("/willpower-drank/api/transactions")
    suspend fun sendTransaction(
            @Field("fromAddress") fromAddress: String,
            @Field("toAddress") toAddress: String,
            @Field("amount") amount: String
    ): Response<SendTransaction>
}