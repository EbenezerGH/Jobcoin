package com.api.domain.transactionhistory

import retrofit2.Response
import retrofit2.http.GET

interface TransactionHistoryDataSource {
    @GET("/willpower-drank/api/transactions")
    suspend fun getTransactionHistoryAsync(): Response<ArrayList<Transaction>>
}